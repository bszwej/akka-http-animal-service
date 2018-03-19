package com.example

import akka.actor.ActorSystem
import akka.event.slf4j.SLF4JLogging
import akka.http.scaladsl.Http
import akka.stream.{ActorMaterializer, Materializer}
import com.example.api.Api
import com.example.config.AppConfig
import com.example.controller.AnimalControllerModule
import com.example.repository.RepositoryModule
import kamon.Kamon
import kamon.jaeger.JaegerReporter

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

object Microservice
  extends App
    with Api
    with RepositoryModule
    with AnimalControllerModule
    with AppConfig
    with SLF4JLogging {

  Kamon.addReporter(new JaegerReporter)

  implicit val system: ActorSystem = ActorSystem("AkkaHttpCatService")
  implicit val materializer: Materializer = ActorMaterializer()
  override implicit val executionContext: ExecutionContext = system.dispatcher

  val serverBinding = Http().bindAndHandle(routes, serverInterface, serverPort)

  serverBinding.onComplete {
    case Success(_) ⇒
      log.info(s"Server started: {}:{}", serverInterface, serverPort)
    case Failure(e) ⇒
      log.error("Error during server binding: {}", e.getMessage)
  }

  sys.addShutdownHook {
    serverBinding
      .flatMap(_.unbind())
      .onComplete(_ ⇒ system.terminate())
  }
}
