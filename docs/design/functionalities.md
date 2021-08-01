# Functionalities
This document will describe all the different functionalities that 
Sharq platform could provide.

### References
https://www.infoq.com/articles/multi-runtime-microservice-architecture/
https://developpaper.com/mecha-carry-mesh-to-the-end/

## Mecha runtime implementation
Application level distributed primitives abstraction layer

# Stateful application functionalities
Also known as distributed primitives 
https://cloud.atomix.io/
https://github.com/atomix
https://github.com/atomix/atomix-controller

## Database abstraction via GraphQL interface
Part of being a Mecha runtime is providing APIs and utilities for
writting applications. 
One way of managing application state is through databases. 
GraphQL crud servers are a generic and abstracted way to access databases.
I should be language agnostic for client applications and the underliying database could be a relational database or a NoSQL database like MongoDB.
Hasura, GraphQL CRUD

## Cache API
Providing a Key-Value store is another utility for storing application state.
Also a cache API will allow to abstract to the specific system used for storing the data.
Redis, Dapr. Atomix?

## Scheduled jobs
The runtime could handle the triggering of workloads periodically
Maybe implement a task system on top of basic distributed primitives, such as key/value stores and distributed locks or leader election primitives...
If implemented in java, use quartz + SQL database to implement the task system.
Knative Eventing?

## Workflow management
Maybe dependent of event-driven capabilities.
The runtime could orchestate the invocation of applications based on a stateful workflow.
Orchestation is coordinated with basic distributed primitives.
Knative Eventing may solve this

# Networking functionalities

## Authentication and authorization
Offloaded authentication and authorization capabilities in a proxy.
Authorino
https://github.com/Kuadrant

## Message transformation
Custom or pre-defined message transformations.
Avro deserialization (to JSON serialization), maybe using a Schema Registry
Modification of JSON payloads, default values, mappings,...
Apicurio for Avro
Maybe camel for transformations, or templates

## Rate limiting
Apply rate limits to application API usage using a proxy.
Envoy, Limitador.
https://github.com/Kuadrant

## Networking Observability
Automatically gather metrics of API usage both ingress and egress
Mecha acts as a proxy for inbound requests and could act as a proxy for outbound requests
If envoy is used, envoy may expose those

## Service to Service invocation
Mecha should provide an API to make http requests to another application.
The benefit from using this API is the possibility of extending client communication with things like retries, circuit breaking, authentication,...
This should enable egress proxy functionalities, like transformations, load balancing, client-side rate limiting...
Dapr, Vertx proxy, Envoy
Custom impl , or istio or other service mesh

# Application binding

## Event-Driven workloads
Applications could be triggered in response to events
Kafka, Knative Eventing, Cloud Events

## Outbound bindings
The runtime could provide a generic API to produce events to various external systems
Dapr, Knative Eventing, Cloud Events or custom impl

## Pub Sub messaging
Offloaded and abstracted messaging capabilities for the application.
Options: application provides topics to subscribe to or subscriptions are declared only in the configuration
Application is called from the runtime for message consumtion
Kafka, Mqtt
Dapr, Knative Eventing or custom impl

# Better developer experience

## Unified or streamlined configuration
The project could provide a unified or streamlined way to deploy and configure the system.
The infrastructure components required to support the whole Mecha runtime should be easily deployable.
Applications deployment and integration with the Mecha runtime should be unified and easy.
Custom controller to integrate all the configuration aspects,
KubeVela to abstract all the configuration aspects, and abstract the deployment.

## Support for local deployment mode
To ease testing and demos the entire ecosystem could be deployed as a single unit and user applications 
could use a central Mecha runtime

## Bindings to CI/CD
The ecosystem could ease the bootstrapping of applications by helping with the creation of CI pipelines
Kam, Tekton, ArgoCD

## Declarative configurations
Everything have to be configured declaratively and beforehand.
This will allow for deterministic deploys, ease of production deployments and enable GitOps

## Heroku like experience
If possible an interface to do very simple deployments could be nice