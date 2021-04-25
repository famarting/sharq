# Design

This document tries to describe the main design components of this project and the complete user story of the problem this project aims to solve.


## User Story 

Full development lifecycle:

* Create app, lots of options:
    * https://code.quarkus.io/
    * yeoman
    * whatever tool you know and like

* Build and unit-test the app

    * whatever is required for your language of choice

    * build container image, options:
        * docker
        * podman

* Run the app locally

    * deployment of your app
        * just by running your binaries
        * or with container image based, options:
            * docker manually
            * docker-compose
        * "injection" of the sidecars and proxies to your app, options:
            * shared proxies deployed with docker via the Sharq cli
    * deployment of the Sharq infra
        * key-value store
            * shared store deployed with docker via the Sharq cli
        * inbound-outbound proxies (messaging focused ATM)
            * shared proxies deployed with docker via the Sharq cli
        * shared infinispan,kafka,... deployed with docker via the Sharq cli
            * issue: how to make this extensible? and not make the Sharq cli tied to infinispan or kafka... ??

* Run the app in kubernetes

    * deployment of your app, options:
        * native kubernetes resources
        * knative
        * OAM (Kubevela)
    * deployment of the Sharq infra
        * key-value store
            * Sharq operator to deploy the store sidecar
            * deploy infinispan,redis,... manually or with third party operators
        * inbound-outbound proxies (messaging focused ATM)
            * knative eventing (api compatible with our inbound and outbound proxies)
            * Sharq operator (to be decided if we implement it) to deploy the proxy sidecar



