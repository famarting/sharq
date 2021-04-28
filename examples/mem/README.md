# In-Memory all in one example

Run the consumer application, this will log the messages we send
```
mvn quarkus:dev -pl utils/consumer/
```

Run the proxy
```
SHARQ_INBOUND_TEST_ENABLE=true mvn quarkus:dev -pl components/mem/
```

Send a message
```
$ http :10001/apis/outbound/v1/test hello=world333
HTTP/1.1 200 OK
Content-Type: */*
content-length: 0

```


Key value store
```
$ http :10001/apis/keyvaluestore/v1/test?key=hello
HTTP/1.1 404 Not Found
Content-Type: application/json
content-length: 0



$ http POST :10001/apis/keyvaluestore/v1/test?key=hello text=world
HTTP/1.1 200 OK
content-length: 0



$ http :10001/apis/keyvaluestore/v1/test?key=hello
HTTP/1.1 200 OK
Content-Type: application/json
content-length: 17

{
    "text": "world"
}
```
