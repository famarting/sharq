# Kafka Inbound Outbound message passing and Key Value Store example 

Run kafka proxy
```
SHARQ_INBOUND_TEST_TOPIC=test1 SHARQ_OUTBOUND_TEST_TOPIC=test1 SHARQ_STORE_TEST_TOPIC=state-topic SHARQ_STORES_TOPICS=state-topic KAFKA_BOOTSTRAP_SERVERS=localhost:9092 mvn quarkus:dev -pl components/kafka/
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

Test key value store

```
$ http :10080/apis/keyvaluestore/v1/test?key=hello
HTTP/1.1 404 Not Found
Content-Type: application/json
content-length: 0

$ http POST :10080/apis/keyvaluestore/v1/test?key=hello text=world
HTTP/1.1 201 Created
content-length: 0

$ http :10080/apis/keyvaluestore/v1/test?key=hello
HTTP/1.1 200 OK
Content-Type: application/json
content-length: 17

{
    "text": "world"
}

$ http DELETE :10080/apis/keyvaluestore/v1/test?key=hello
HTTP/1.1 200 OK
Content-Type: application/json
content-length: 0

```