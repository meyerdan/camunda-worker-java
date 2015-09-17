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
package org.camunda.bpm.ext.sdk;

import java.net.URI;

import org.apache.http.conn.HttpHostConnectException;
import org.camunda.bpm.ext.sdk.impl.WorkerRegistrationImpl;
import org.camunda.commons.logging.BaseLogger;

/**
 * @author Daniel Meyer
 *
 */
public class ClientLogger extends BaseLogger {

  public static final String PROJECT_CODE = "CAMCLIENT";
  public static final String PROJECT_LOGGER= "org.camunda.bpm.ext.client";

  public static ClientLogger LOGGER = createLogger(ClientLogger.class, PROJECT_CODE, PROJECT_LOGGER, "01");

  public void initializingCamundaClient(String endpointUrl) {
    logInfo("001", "Initializing Camunda Client for endpoint '{}'.", endpointUrl);
  }

  public void closing() {
    logInfo("002", "Closing Camunda Client.");
  }

  public void registeredNewWorker(WorkerRegistrationImpl registration) {
    logInfo("003", "Registered new worker [topic='{}',class='{}']",
        registration.getTopicName(),
        registration.getWorker().getClass().getName());
  }

  public void exceptionDuringPoll(Exception e) {
    logError("004", "Exception while polling", e);
  }

  public void unableToConnect(URI uri, HttpHostConnectException e) {
    logError("005", "Unable to connect to host '{}'.", uri.getHost());
  }

  public void unableToPoll(CamundaClientException e) {
    logError("006", "Exception while executing multi-poll: {}", e.getMessage());
  }

  public void backOff(int currentWait) {
    logInfo("007", "Poll-Backoff: {} seconds.", currentWait/1000 );
  }

  public void workerException(Worker w, Exception e) {
    logError("008", "Exception in workder {}: ",w.getClass().getName(), e);
  }



}
