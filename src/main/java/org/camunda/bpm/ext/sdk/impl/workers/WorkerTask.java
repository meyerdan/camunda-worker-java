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
import java.util.Map;

import org.apache.http.client.methods.HttpPost;
import org.camunda.bpm.engine.impl.core.variable.VariableMapImpl;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.bpm.engine.variable.value.TypedValue;
import org.camunda.bpm.ext.sdk.ClientLogger;
import org.camunda.bpm.ext.sdk.TaskContext;
import org.camunda.bpm.ext.sdk.Worker;
import org.camunda.bpm.ext.sdk.impl.ClientCommandContext;
import org.camunda.bpm.ext.sdk.impl.ClientCommandExecutor;
import org.camunda.bpm.ext.sdk.impl.ClientPostComand;
import org.camunda.bpm.ext.sdk.impl.dto.CompleteTaskRequestDto;
import org.camunda.bpm.ext.sdk.impl.dto.TaskFailedRequestDto;

/**
 * @author Daniel Meyer
 *
 */
public class WorkerTask implements TaskContext, Runnable {

  private static ClientLogger LOG = ClientLogger.LOGGER;

  protected String taskId;

  protected VariableMap retreivedVariables;
  protected VariableMap writtenVariables = new VariableMapImpl();


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
        reqDto.setVariables(ctc.writeVariables(Variables.fromMap(variables)));

        post.setEntity(ctc.writeObject(reqDto));

        ctc.execute(post);

        return null;
      }
    });
  }

  public void taskFailed() {
    taskFailed(null);
  }

  public void taskFailed(final String errorMessage) {
    clientCommandExecutor.executePost("/external-task/"+taskId+"/failed", new ClientPostComand<Void>() {
      public Void execute(ClientCommandContext ctc, HttpPost post) {

        TaskFailedRequestDto reqDto = new TaskFailedRequestDto(ctc.getClientId(), errorMessage);
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

  public VariableMap getVariables() {
    return retreivedVariables;
  }

  public <T> T getVariable(String name) {
    return (T) retreivedVariables.get(name);
  }

  public <T extends TypedValue> T getVariableTyped(String name) {
    return retreivedVariables.getValueTyped(name);
  }

  public String getTaskId() {
    return taskId;
  }

  public void run() {
    try {
      worker.doWork(this);
    }
    catch(Exception e){
      LOG.workerException(worker, e);
      // automatically fail the task
      taskFailed(e.getMessage());
    }
  }

  public static WorkerTask from(LockedTaskDto lockedTaskDto, ClientCommandExecutor clientCommandExecutor, Worker worker) {
    WorkerTask workerTask = new WorkerTask();
    workerTask.taskId = lockedTaskDto.getId();
    workerTask.clientCommandExecutor = clientCommandExecutor;
    workerTask.worker = worker;
    workerTask.retreivedVariables = clientCommandExecutor.getValueSerializers()
        .readValues(lockedTaskDto.getVariables(), clientCommandExecutor.getObjectMapper());
    return workerTask;
  }


}
