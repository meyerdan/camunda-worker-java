# camunda-worker-java

Implement workers for external tasks in [Camunda BPM](http://camunda.org) in Java.

## Summary

This tool provides a JavaScript interface to external tasks exposed by the process engine.

## Getting started

Example of a Java consumer

```java
public class MyApp {

  public static void main(String[] args) {

    CamundaClient client = CamundaClient.create()
      .endpointUrl("http://localhost:8080/engine-rest/")
      .build();

    WorkerRegistration registration = client.registerWorker()
      .topicName("reserveOrderItems")
      .lockTime(5000)
      .worker(new Worker() {

        public void doWork(TaskContext taskContext) {

          // do the work

          taskContext.complete();

        }
      })
      .build();

  }

}
```

## Maven dependency

```xml
<dependency>
  <groupId>org.camunda.bpm.ext</groupId>
  <artifactId>camunda-bpm-sdk-java</artifactId>
  <version>0.0.1-SNAPSHOT</version>
</dependency>
```

## Features

* **Efficient Threading & Multipoll**: single thread polling multiple topics at once delegating to a pool of worker threads
* **Exponential Backoff**: poller backs off if no work is available

