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

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ByteArrayEntity;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.ext.sdk.CamundaClientException;
import org.camunda.bpm.ext.sdk.impl.variables.TypedValueDto;
import org.camunda.bpm.ext.sdk.impl.variables.ValueSerializers;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Daniel Meyer
 *
 */
public class ClientCommandContext {

  protected HttpClient httpClient;
  protected ObjectMapper objectMapper;
  protected String clientId;
  protected ValueSerializers valueSerializers;

  public ClientCommandContext(HttpClient client, ObjectMapper objectMapper, String clientId, ValueSerializers valueSerializers) {
    this.httpClient = client;
    this.objectMapper = objectMapper;
    this.clientId = clientId;
    this.valueSerializers = valueSerializers;
  }

  public HttpClient getHttpClient() {
    return httpClient;
  }

  public void setHttpClient(HttpClient client) {
    this.httpClient = client;
  }

  public ObjectMapper getObjectMapper() {
    return objectMapper;
  }

  public HttpEntity writeObject(Object value) {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    try {
      objectMapper.writeValue(out, value);
      return new ByteArrayEntity(out.toByteArray());
    } catch (Exception e) {
      throw new CamundaClientException("Exception while serializing object as json", e);
    }
  }

  public HttpResponse execute(HttpRequestBase post) {
    HttpResponse response = null;
    try {
      response = httpClient.execute(post);
    } catch (Exception e) {
      throw new CamundaClientException("Exception while executing request", e);
    }

    int statusCode = response.getStatusLine().getStatusCode();
    if(statusCode < 200 || statusCode >= 300) {
      HttpEntity entity = response.getEntity();
      String responseStr = "";
      try {
        InputStream content = entity.getContent();
        BufferedReader reader = new BufferedReader(new InputStreamReader(content));
        while(reader.ready()) {
          responseStr += reader.readLine();
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
      throw new CamundaClientException("Request "+post + " returned error: "+ response.getStatusLine()+ ": "+responseStr);
    }

    return response;
  }

  public <T> T readObject(HttpEntity entity, Class<T> type) {
    try {
      InputStream content = entity.getContent();
      return objectMapper.readValue(content, type);
    } catch (Exception e) {
      throw new CamundaClientException("Exception while deserializing json object", e);
    }
  }

  public String getClientId() {
    return clientId;
  }

  public void setClientId(String clientId) {
    this.clientId = clientId;
  }

  public ValueSerializers getValueSerializers() {
    return valueSerializers;
  }

  public Map<String, TypedValueDto> writeVariables(VariableMap vars) {
    return valueSerializers.writeValues(vars, objectMapper);
  }

}
