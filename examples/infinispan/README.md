# Infinispan Key-Value store example

```
$ http :10070/apis/keyvaluestore/v1/test?key=hello
HTTP/1.1 404 Not Found
Content-Type: application/json
content-length: 0

$ http POST :10070/apis/keyvaluestore/v1/test?key=hello text=world
HTTP/1.1 201 Created
content-length: 0

$ http :10070/apis/keyvaluestore/v1/test?key=hello
HTTP/1.1 200 OK
Content-Type: application/json
content-length: 17

{
    "text": "world"
}

$ http DELETE :10070/apis/keyvaluestore/v1/test?key=hello
HTTP/1.1 200 OK
Content-Type: application/json
content-length: 0

```
