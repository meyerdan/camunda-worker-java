/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.camunda.bpm.ext.sdk.impl.workers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.camunda.bpm.ext.sdk.CamundaClientException;
import org.camunda.bpm.ext.sdk.ClientLogger;
import org.camunda.bpm.ext.sdk.Worker;
import org.camunda.bpm.ext.sdk.impl.ClientCommandContext;
import org.camunda.bpm.ext.sdk.impl.ClientCommandExecutor;
import org.camunda.bpm.ext.sdk.impl.ClientPostComand;
import org.camunda.bpm.ext.sdk.impl.WorkerRegistrationImpl;

/**
 * @author Daniel Meyer
 *
 */
public class PollTasksRunnable implements Runnable {

  private final static ClientLogger LOG = ClientLogger.LOGGER;

  protected transient boolean exit = false;

  protected WorkerManager workerManager;
  protected ClientCommandExecutor commandExecutor;
  protected BackoffStrategy backoffStrategy;

  public PollTasksRunnable(WorkerManager workerManager, ClientCommandExecutor commandExecutor, BackoffStrategy backoffStrategy) {
    this.workerManager = workerManager;
    this.commandExecutor = commandExecutor;
    this.backoffStrategy = backoffStrategy;
  }

  public void run() {
    while(!exit) {
      acquire();
    }
  }

  protected void acquire() {
    final List<WorkerRegistrationImpl> registrations = workerManager.getRegistrations();
    final MultiPollRequestDto request = new MultiPollRequestDto();
    final Map<String, Worker> workerMap = new HashMap<String, Worker>();

    request.clear();
    workerMap.clear();


    synchronized (registrations) {
      int numOfRegistrations = registrations.size();

      if(numOfRegistrations == 0) {
        try {
          registrations.wait();
        } catch (InterruptedException e) {
          e.printStackTrace();
          // TODO
        }
      }

      for (WorkerRegistrationImpl registration : registrations) {
        request.topics.add(new PollInstructionDto(registration.getTopicName(),
            registration.getLockTime(),
            registration.getVariableNames()));
        workerMap.put(registration.getTopicName(), registration.getWorker());
      }

    }

    int tasksAcquired = 0;

    try {
      tasksAcquired = poll(request, workerMap);
    } catch(Exception e) {
      LOG.exceptionDuringPoll(e);
    }

    if(tasksAcquired == 0) {
      try {
        // back-off
        backoffStrategy.run();
      } catch(InterruptedException e) {
        e.printStackTrace();
      }
    }
    else {
      backoffStrategy.reset();
    }
  }

  private int poll(final MultiPollRequestDto request, final Map<String, Worker> workerMap) {
    return commandExecutor.executePost("/external-task/multi-poll", new ClientPostComand<Integer>() {

      public Integer execute(ClientCommandContext ctc, HttpPost post) {

        request.setConsumerId(ctc.getClientId());
        request.setMaxTasks(10);

        post.setEntity(ctc.writeObject(request));

        int tasksAcquired = 0;
        try {
          HttpResponse response = ctc.execute(post);
          LockedTasksResponseDto lockedTasksResponseDto = ctc.readObject(response.getEntity(), LockedTasksResponseDto.class);


          for (LockedTaskDto lockedTaskDto : lockedTasksResponseDto.getTasks()) {

            WorkerTask task = WorkerTask.from(lockedTaskDto, commandExecutor, workerMap.get(lockedTaskDto.getTopicName()));
            workerManager.execute(task);
            tasksAcquired++;
          }
        }
        catch(CamundaClientException e) {
          LOG.unableToPoll(e);
        }

        return tasksAcquired;
      }
    });
  }

  public void exit() {
    exit = true;
    // thread may be either waiting for a registration to open
    synchronized (workerManager.getRegistrations()) {
      workerManager.getRegistrations().notifyAll();
    }
    // or doing backoff
    backoffStrategy.stopWait();
  }

}
