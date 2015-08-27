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

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.camunda.bpm.ext.sdk.impl.ClientCommandExecutor;
import org.camunda.bpm.ext.sdk.impl.workers.BackoffStrategy;
import org.camunda.bpm.ext.sdk.impl.workers.SimpleBackoffStrategy;
import org.camunda.bpm.ext.sdk.impl.workers.WorkerManager;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Daniel Meyer
 *
 */
public class CamundaClientBuilder {

  protected int numOfWorkerThreads = 2;
  protected int queueSize = 25;
  protected BackoffStrategy backoffStrategy;

  protected String endpointUrl;
  protected CloseableHttpClient httpClient;
  protected ClientCommandExecutor clientCommandExecutor;
  protected WorkerManager workerManager;
  protected ObjectMapper objectMapper;
  protected String clientId;

  public CamundaClientBuilder() {

  }

  public CamundaClientBuilder endpointUrl(String endpointUrl) {
    this.endpointUrl = endpointUrl;
    return this;
  }

  public CamundaClientBuilder clientId(String clientId) {
    this.clientId = clientId;
    return this;
  }

  public CamundaClientBuilder consumerId(int numOfWorkerThreads) {
    this.numOfWorkerThreads = numOfWorkerThreads;
    return this;
  }

  public CamundaClientBuilder queueSize(int queueSize) {
    this.queueSize = queueSize;
    return this;
  }


   // building ///////////////////////////////

  public CamundaClient build() {
    init();
    return new CamundaClient(this);
  }

  protected void init() {
    initClientId();
    initBackoffStrategy();
    initObjectMapper();
    initHttpClient();
    initClientCommandExecutor();
    initWorkerManager();
  }

  protected void initBackoffStrategy() {
    if(backoffStrategy == null) {
      backoffStrategy = new SimpleBackoffStrategy();
    }
  }

  protected void initClientId() {
    if(clientId == null) {
      String hostName;
      try {
        hostName = InetAddress.getLocalHost().getHostName() + " - " + UUID.randomUUID().toString();
        clientId = hostName;
      } catch (UnknownHostException e) {
        throw new CamundaClientException("Cannot get hostname", e);
      }
    }
  }

  protected void initObjectMapper() {
    if(objectMapper == null) {
      objectMapper = new ObjectMapper();
      objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
  }

  protected void initWorkerManager() {
    if(this.workerManager == null) {
      this.workerManager = new WorkerManager(clientCommandExecutor, numOfWorkerThreads, queueSize, backoffStrategy);
    }
  }

  protected void initClientCommandExecutor() {
    if(clientCommandExecutor == null) {
      clientCommandExecutor = new ClientCommandExecutor(httpClient, endpointUrl, clientId, objectMapper);
    }
  }

  protected void initHttpClient() {
    if(httpClient == null) {
      httpClient = HttpClients.createDefault();
    }
  }
}
