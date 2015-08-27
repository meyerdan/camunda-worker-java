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

/**
 * @author Daniel Meyer
 *
 */
public class SimpleBackoffStrategy implements BackoffStrategy {

  // config
  int waitIncrease = 2;
  int maxWait =  60 * 1000;
  int minWait = 1000;

  // state
  int currentWait = minWait;

  public SimpleBackoffStrategy() {

  }

  public SimpleBackoffStrategy(int waitIncrease, int maxWait, int minWait) {
    this.waitIncrease = waitIncrease;
    this.maxWait = maxWait;
    this.minWait = minWait;
  }

  public void run() throws InterruptedException {
    currentWait = Math.min(maxWait, currentWait * waitIncrease);
    System.out.println("waiting for "+currentWait);
    Thread.sleep(currentWait);
  }

  public void reset() {
    currentWait = minWait;
  }

}
