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
package org.camunda.bpm.ext.sdk.dto;

/**
 * @author Daniel Meyer
 *
 */
public class StartProcessInstanceResponseDto {

  protected String id;
  protected String definitionId;
  protected String businessKey;
  protected boolean ended;
  protected boolean suspended;

  public String getId() {
    return id;
  }
  public void setId(String id) {
    this.id = id;
  }
  public String getDefinitionId() {
    return definitionId;
  }
  public void setDefinitionId(String definitionId) {
    this.definitionId = definitionId;
  }
  public String getBusinessKey() {
    return businessKey;
  }
  public void setBusinessKey(String businessKey) {
    this.businessKey = businessKey;
  }
  public boolean isEnded() {
    return ended;
  }
  public void setEnded(boolean ended) {
    this.ended = ended;
  }
  public boolean isSuspended() {
    return suspended;
  }
  public void setSuspended(boolean suspended) {
    this.suspended = suspended;
  }

}
