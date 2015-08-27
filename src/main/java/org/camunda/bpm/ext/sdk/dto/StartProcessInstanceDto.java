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

import java.util.Map;

import org.camunda.bpm.ext.sdk.impl.variables.TypedValueDto;

/**
 * @author Daniel Meyer
 *
 */
public class StartProcessInstanceDto {

  protected Map<String, TypedValueDto> variables;
  protected String businessKey;
  protected String caseInstanceId;

  public Map<String, TypedValueDto> getVariables() {
    return variables;
  }
  public void setVariables(Map<String, TypedValueDto> variables) {
    this.variables = variables;
  }
  public String getBusinessKey() {
    return businessKey;
  }
  public void setBusinessKey(String businessKey) {
    this.businessKey = businessKey;
  }
  public String getCaseInstanceId() {
    return caseInstanceId;
  }
  public void setCaseInstanceId(String caseInstanceId) {
    this.caseInstanceId = caseInstanceId;
  }
}
