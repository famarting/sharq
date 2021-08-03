package main

import (
	"context"
	"fmt"
	"log"
	"time"

	atomixclient "github.com/atomix/atomix-go-client/pkg/atomix"
	atomixlock "github.com/atomix/atomix-go-client/pkg/atomix/lock"

	cloudevents "github.com/cloudevents/sdk-go/v2"
)

func main() {
	fmt.Println("Hello world")

	client := atomixclient.NewClient(atomixclient.WithBrokerHost("atomix-controller.kube-system.svc.cluster.local"), atomixclient.WithBrokerPort(5679))

	lock, err := client.GetLock(context.TODO(), "my-lock")
	if err != nil {
		panic(err)
	}

	_, err = lock.Lock(context.TODO(), atomixlock.WithTimeout(3*time.Second))
	if err != nil {
		panic(err)
	}

	c, err := cloudevents.NewClientHTTP()
	if err != nil {
		log.Fatalf("failed to create client, %v", err)
	}

	// Create an Event.
	event := cloudevents.NewEvent()
	event.SetSource("example/uri")
	event.SetType("example.type")
	event.SetData(cloudevents.ApplicationJSON, map[string]string{"hello": "world"})

	// Set a target.
	ctx := cloudevents.ContextWithTarget(context.Background(), "http://localhost:8080/")

	// Send that Event.
	if result := c.Send(ctx, event); cloudevents.IsUndelivered(result) {
		log.Fatalf("failed to send, %v", result)
	}

	lock.Unlock(context.TODO())

}
