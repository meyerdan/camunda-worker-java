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
package org.camunda.bpm.ext.sdk.impl;

import java.util.ArrayList;
import java.util.List;

import org.camunda.bpm.ext.sdk.Worker;
import org.camunda.bpm.ext.sdk.WorkerRegistration;
import org.camunda.bpm.ext.sdk.impl.workers.WorkerManager;

/**
 * @author Daniel Meyer
 *
 */
public class WorkerRegistrationImpl implements WorkerRegistration {

  protected String topicName;
  protected Worker worker;
  protected Integer lockTime;
  protected WorkerManager workerManager;
  protected List<String> variableNames = new ArrayList<String>();

  public WorkerRegistrationImpl(WorkerManager workerManager) {
    this.workerManager = workerManager;
  }

  public void remove() {
    workerManager.remove(this);
  }

  public void setTopicName(String topicName) {
    this.topicName = topicName;
  }

  public void setWorker(Worker worker) {
    this.worker = worker;
  }

  public WorkerManager getWorkerManager() {
    return workerManager;
  }

  public void setWorkerManager(WorkerManager workerManager) {
    this.workerManager = workerManager;
  }

  public String getTopicName() {
    return topicName;
  }

  public Worker getWorker() {
    return worker;
  }

  public Integer getLockTime() {
    return lockTime;
  }

  public void setLockTime(Integer lockTime) {
    this.lockTime = lockTime;
  }

  public List<String> getVariableNames() {
    return variableNames;
  }

}
