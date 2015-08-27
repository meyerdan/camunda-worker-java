import org.camunda.bpm.engine.variable.Variables;
import org.camunda.bpm.ext.sdk.CamundaClient;
import org.camunda.spin.json.SpinJsonNode;

import static org.camunda.spin.plugin.variable.SpinValues.*;
import static org.camunda.bpm.engine.variable.Variables.*;
import static org.camunda.spin.Spin.*;

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
public class Producer {

  public static void main(String[] args) throws InterruptedException {

    CamundaClient client = CamundaClient.create()
      .endpointUrl("http://localhost:8080/engine-rest/")
      .build();

    client.createDeployment()
      .name("my-deployment")
      .classPathResource("processOrder.bpmn")
      .deploy();

    while(true) {

      SpinJsonNode customer = JSON("{}")
        .prop("customerId", "someCustomer")
        .prop("age", 18);


      client.startProcessInstanceByKey("orderProcess", Variables.createVariables()
        .putValue("stringVar", "stringVal")
        .putValue("customerVar", jsonValue(customer).create())
        .putValue("object", objectValue(new CustomObject())));

    }


  }

}
