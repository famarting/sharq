## Knative eventing management abstraction

Create topic and topic subscription resources to abstract knative resources
Enforce RBAC for gatekeeping and control of what applications can access certaing topics

## Dapr like platform
Dapr like platform but coding applications with as less custom libraries as possible
pubsub and bindings: just leverage http for sending and receiving events with knative
state: just use whatever database you need with your favourite ORM
primitives: use k8s native ones, cronjob, lease,...

## kafka streams sidecar for distributed state stores
Abstract kafka streams with a simple REST API and deploy kstreams instances as sidecars to get distributed stores out of the box