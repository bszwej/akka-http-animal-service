# Akka Http Animal Service

This repository contains a simple CRUD (micro)service for managing animals.

## Overview 

The goal of this project is to show how to build a (micro)service using Scala and Akka Http. 

In this project, you can find the following things:
- marshalling/unmarshalling http JSON responses/requests with circe
- error handling
- using Monad Transformer in a real-world use case
- Kubernetes deployment with Helm chart
- unit, integration and e2e tests suites
- request tracing with Kamon and Jaeger
- ... and more!

## Running

1. Clone the repository
```bash
git clone https://github.com/bszwej/akka-http-animal-service.git
```

2. Change dir 
```bash
cd akka-http-animal-service
```

3. Run docker compose using sbt. It spins up the microservice, MongoDB and Jaeger. 
```bash
sbt dockerComposeUp
```

4. Make some calls:
```bash
curl -X POST \
	-H 'Content-Type: application/json' \
	-d '{"name": "Charlie", "kind": "Unicorn", "age": 21}' \
	http://localhost:8080/animals
curl http://localhost:8080/animals
```

5. Now go to Jaeger dashboard (http://localhost:16686) and see new traces.

You can find the API documentation [here](./docs/api.yaml).

In order to stop: `sbt dockerComposeStop`.

## Deployment

In the `deployment` directory, you can find Helm chart.

Install on Kubernetes using Helm chart:

1. Install MongoDB
```bash
helm install --name default-mongo stable/mongodb
```

2. Install Jaeger (standalone version, one container)
```bash
helm install --name jaeger-standalone deployment/jaeger
```

3. Install the microservice
```bash
helm install --name akka-http-animal-service deployment/microservice
```

This is not a production deployment. However, it can be useful to play with locally on Minikube.

When running on Minikube:

- Service is accessible under `http://192.168.99.100:31000`
- Jaeger dashboard is accessible under `http://192.168.99.100:31001`

## Testing

This project contains unit, integration and e2e tests.

- run unit & integration `sbt test`
- run unit tests only `sbt "testOnly * -- -l tags.RequiresDb"`
- run integration tests only `sbt "testOnly * -- -n tags.RequiresDb"`

In order to run e2e tests:
1. Open up sbt shell: `sbt` 
1. `dockerComposeUp`
1. `e2e:test` 
1. `dockerComposeStop`

You can point e2e tests to a different host with `url` flag:
- `sbt -Durl=http://192.168.99.100:31000 e2e:test`

## Jaeger tracing

This project uses Kamon to trace requests. After running the project with `sbt dockerComposeUp`, Jaeger dashboard can be found under http://localhost:16686/.

![](jaeger-trace-list-screen.png)


![](jaeger-trace-screen.png)
