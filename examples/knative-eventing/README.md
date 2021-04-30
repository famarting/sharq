# Example knative eventing

Run sharq, this will run all sharq components in a container and will subscribe to the eventing system the following endpoint http://localhost:8080/events
```
./cli/sharq run --sub :8080/events
```

Run the app 
```
mvn quarkus:dev -pl examples/users-service/
```

Test the app
```
http :8080/users/ username=admin password=admin
```

The users-service app will use the key-value store in sharq to store the user object, and after that the users-service app
will send a message to `http://localhost:10001/apis/outbound/v1/default`. The Sharq platform will receive that message and because when running sharq we provided a subscription it will forward the message to
`http://localhost:8080/events`.

Note: Running `./cli/sharq run --sub :8080/events` is the same as `./cli/sharq run --sub default::8080/events` or `./cli/sharq run --sub default:localhost:8080/events`