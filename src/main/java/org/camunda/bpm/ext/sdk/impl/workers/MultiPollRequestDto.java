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

import java.util.ArrayList;
import java.util.List;

/**
 * @author Daniel Meyer
 *
 */
public class MultiPollRequestDto {

  protected int maxTasks;

  protected String consumerId;

  protected List<PollInstructionDto> topics = new ArrayList<PollInstructionDto>();

  public int getMaxTasks() {
    return maxTasks;
  }

  public void setMaxTasks(int maxTasks) {
    this.maxTasks = maxTasks;
  }

  public String getConsumerId() {
    return consumerId;
  }

  public void setConsumerId(String consumerId) {
    this.consumerId = consumerId;
  }

  public List<PollInstructionDto> getTopics() {
    return topics;
  }

  public void setTopics(List<PollInstructionDto> topics) {
    this.topics = topics;
  }

  public void clear() {
    topics.clear();
  }

}
