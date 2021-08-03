# MVP 1 - Sharq platform

Curated set of functionalities and implementation decisions for a minimal proof of concept of this project.

+ Distributed primitives with Atomix. Leverage Atomix Kubernetes Controller and Atomix REST APIs to provide distributed primitives
+ Out of the box authentication and authorization. Leverage Authorino to provide security capabilities.
+ Event Driven workloads. Use Knative
+ Simplified deployment and configuration. Abstract all configurations with KubeVela or Crossplane

# MVP 2 - Sarqd platform 

Sharqd platform is a Dapr like microservices runtime.
For the first version it will be kafka centric to ease development.

Features:
+ Abstracted Pub/Sub using sidecar
+ Abstracted distributed key/value store using sidecar with REST API
