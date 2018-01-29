# Akka Http Animal Service

This repository contains a simple CRUD microservice for managing animals, which uses MongoDB.

The goal of this project is to show how to build a microservice using Scala and Akka Http. 

In this example, you can see how to:
- marshal/unmarshal http JSON responses/requests with circe
- handle errors
- use Monad Transformer in real-world example
- deploy it on Kubernetes using Helm chart
- prepare e2e tests suite and configure SBT to run them
- trace requests using Kamon and Jaeger
- and many more!

## Running

1. `git clone https://github.com/bszwej/akka-http-animal-service.git`
1. `cd akka-http-animal-service`
1. `sbt dockerComposeUp`

It'll run docker-compose and spin up the microservice as well as MongoDB instance. 
You can find the API documentation [here](./docs/api.yaml).

## Deployment

In the `deployment` directory, you can find a Helm chart. You can use it to deploy this microservice on k8s cluster.

Install on Kubernetes w/ Helm chart:

1. `helm install --name default-mongo stable/mongodb`
1. `helm install --name akka-http-animal-service deployment/helm`

This is obviously not a production deployment. However, it can be useful to play with locally on Minikube.

## Testing

This project contains unit, integration and e2e tests.

- run unit & integration `sbt test`
- run unit tests only `sbt "testOnly * -- -l tags.RequiresDb"`
- run integration tests only `sbt "testOnly * -- -n tags.RequiresDb"`

In order to run e2e tests:
1. Open up sbt shell: `sbt` 
1. `dockerComposeUp`
1. `e2e:tests` 

You can point e2e tests to a different host with `url` flag:
- `sbt -Durl=http://animal-svc.com:9088 e2e:test`
