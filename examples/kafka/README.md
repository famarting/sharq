# Kafka Inbound Outbound message passing example

Run kafka proxy
```
SHARQ_INBOUND_TEST_TOPIC=test1 SHARQ_OUTBOUND_TEST_TOPIC=test1 KAFKA_BOOTSTRAP_SERVERS=localhost:32814 mvn quarkus:dev -pl components/kafka/
```

```
$ http :10080/apis/outbound/v1/test hello=world333
HTTP/1.1 200 OK
Content-Type: */*
ce-offset: 9
content-length: 0


```

The kafka proxy will consume the produced kafka message it as send it to localhost:8080/ (the target can be configured, that's the default value)
The messages received are cloud-events 
