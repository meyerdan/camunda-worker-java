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

import java.util.Arrays;
import java.util.UUID;

import org.camunda.bpm.ext.sdk.CamundaClient;
import org.camunda.spin.json.SpinJsonNode;

import static org.camunda.spin.Spin.*;
import static java.util.UUID.*;
import static org.camunda.bpm.engine.variable.Variables.*;
import static org.camunda.spin.plugin.variable.SpinValues.*;




/**
 * @author Daniel Meyer
 *
 */
public class Producer {

  public static void main(String[] args) throws InterruptedException {

    CamundaClient client = CamundaClient.create()
      .endpointUrl("http://192.168.88.216:8080/engine-rest/")
      .build();

    client.createDeployment()
      .name("my-deployment")
      .classPathResource("processOrder.bpmn")
      .enableDuplicateFiltering()
      .deploy();

    while(true) {

      SpinJsonNode order = JSON("{}")
        .prop("orderId", UUID.randomUUID().toString())
        .prop("status", "NEW")
        .prop("orderItems", Arrays.<Object>asList(
            JSON("{}").prop("itemId", randomUUID().toString()),
            JSON("{}").prop("itemId", randomUUID().toString()),
            JSON("{}").prop("itemId", randomUUID().toString())));


      client.startProcessInstanceByKey("orderProcess", createVariables()
          .putValue("order", jsonValue(order)));

      Thread.sleep(500);
    }


  }

}
