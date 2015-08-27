import java.util.Date;

import org.camunda.bpm.engine.variable.value.ObjectValue;
import org.camunda.bpm.ext.sdk.CamundaClient;
import org.camunda.bpm.ext.sdk.TaskContext;
import org.camunda.bpm.ext.sdk.Worker;
import org.camunda.bpm.ext.sdk.WorkerRegistration;
import org.camunda.spin.plugin.variable.value.JsonValue;

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

/**
 * @author Daniel Meyer
 *
 */
public class Consumer {

  public static void main(String[] args) {

    CamundaClient client = CamundaClient.create()
      .endpointUrl("http://localhost:8080/engine-rest/")
      .build();

    WorkerRegistration registration = client.registerWorker()
      .topicName("reserveOrderItems")
      .lockTime(5000)
      .variableNames("stringVar", "customerVar", "object")
      .worker(new Worker() {

        public void doWork(TaskContext taskContext) {

          ObjectValue objectVar = taskContext.getVariableTyped("object");
          if(objectVar != null) {
            CustomObject object = objectVar.getValue(CustomObject.class);
            System.out.println(object);
          }

          String bar = taskContext.getVariable("stringVar");
          if(taskContext.getVariable("customerVar") != null) {
            JsonValue jsonVal = taskContext.getVariableTyped("customerVar");

            Number age = jsonVal.getValue().prop("age").numberValue();
            System.out.println(age);
        }

          if(new Date().getTime() % 13 == 0) {
            taskContext.taskFailed("Task failed");
            System.out.println("Task failed");
          }
          else {
            System.out.println("Task complete");
            taskContext.complete();
          }

        }
      })
      .build();

  }

}
