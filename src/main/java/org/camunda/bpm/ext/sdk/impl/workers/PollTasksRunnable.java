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

import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.camunda.bpm.ext.sdk.impl.ClientCommandContext;
import org.camunda.bpm.ext.sdk.impl.ClientCommandExecutor;
import org.camunda.bpm.ext.sdk.impl.ClientPostComand;
import org.camunda.bpm.ext.sdk.impl.WorkerRegistrationImpl;

/**
 * @author Daniel Meyer
 *
 */
public class PollTasksRunnable implements Runnable {

  protected WorkerManager workerManager;
  protected ClientCommandExecutor commandExecutor;

  public PollTasksRunnable(WorkerManager workerManager, ClientCommandExecutor commandExecutor) {
    this.workerManager = workerManager;
    this.commandExecutor = commandExecutor;
  }

  public void run() {

    final List<WorkerRegistrationImpl> registrations = workerManager.getRegistrations();
    long pollCounter = 0;

    while(true) {

      WorkerRegistrationImpl registration = null;
      synchronized (registrations) {
        int numOfRegistrations = registrations.size();

        if(numOfRegistrations == 0) {
          try {
            registrations.wait();
            continue;
          } catch (InterruptedException e) {
            e.printStackTrace();
            // TODO
          }
        }

        int registrationIndex = (int) (pollCounter % numOfRegistrations);
        registration = registrations.get(registrationIndex);
      }

      poll(registration);
      pollCounter++;
    }
  }

  private void poll(final WorkerRegistrationImpl registration) {
    commandExecutor.executePost("/external-task/poll", new ClientPostComand<Void>() {

      public Void execute(ClientCommandContext ctc, HttpPost post) {

        PollAndLockTaskRequestDto requestDto = new PollAndLockTaskRequestDto();
        requestDto.setTopicName(registration.getTopicName());
        requestDto.setVariableNames(registration.getVariableNames());
        requestDto.setConsumerId(ctc.getClientId());
        requestDto.setLockTimeInSeconds(registration.getLockTime());
        requestDto.setMaxTasks(5);

        post.setEntity(ctc.writeObject(requestDto));

        HttpResponse response = ctc.execute(post);
        LockedTasksResponseDto lockedTasksResponseDto = ctc.readObject(response.getEntity(), LockedTasksResponseDto.class);

        for (LockedTaskDto lockedTaskDto : lockedTasksResponseDto.getTasks()) {
          WorkerTask task = WorkerTask.from(lockedTaskDto, commandExecutor, registration.getWorker());
          workerManager.execute(task);
        }

        return null;
      }
    });
  }

}
