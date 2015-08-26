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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.methods.HttpPost;
import org.camunda.bpm.ext.sdk.TaskContext;
import org.camunda.bpm.ext.sdk.Worker;
import org.camunda.bpm.ext.sdk.impl.ClientCommandContext;
import org.camunda.bpm.ext.sdk.impl.ClientCommandExecutor;
import org.camunda.bpm.ext.sdk.impl.ClientPostComand;
import org.camunda.bpm.ext.sdk.impl.dto.CompleteTaskRequestDto;

/**
 * @author Daniel Meyer
 *
 */
public class WorkerTask implements TaskContext, Runnable {

  protected String taskId;

  protected Map<String, Object> retreivedVariables;
  protected Map<String, Object> writtenVariables = new HashMap<String, Object>();


  protected ClientCommandExecutor clientCommandExecutor;
  protected Worker worker;

  public void complete() {
    complete(Collections.<String, Object>emptyMap());
  }

  public void complete(final Map<String, Object> variables) {
    clientCommandExecutor.executePost("/external-task/"+taskId+"/complete", new ClientPostComand<Void>() {
      public Void execute(ClientCommandContext ctc, HttpPost post) {

        CompleteTaskRequestDto reqDto = new CompleteTaskRequestDto();
        reqDto.setConsumerId(ctc.getClientId());
        reqDto.setVariables(new HashMap<String, Object>());

        post.setEntity(ctc.writeObject(reqDto));

        ctc.execute(post);

        return null;
      }
    });
  }

  public void setVariable(String name, Object value) {
    writtenVariables.put(name, value);
  }

  public void setAllVariables(Map<String, Object> variables) {
    writtenVariables.putAll(variables);
  }

  public Map<String, Object> getVariables() {
    return retreivedVariables;
  }

  public <T> T getVariable(String name) {
    return (T) retreivedVariables.get(name);
  }

  public void run() {
    worker.doWork(this);
  }

  public static WorkerTask from(LockedTaskDto lockedTaskDto, ClientCommandExecutor clientCommandExecutor, Worker worker) {
    WorkerTask workerTask = new WorkerTask();
    workerTask.taskId = lockedTaskDto.getId();
    workerTask.clientCommandExecutor = clientCommandExecutor;
    workerTask.worker = worker;
    return workerTask;
  }


}
