package com.example.api

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.example.controller.AnimalController

import scala.concurrent.ExecutionContext

trait Api extends ExceptionHandling with RejectionHandling with JsonSupport with Directives {

  implicit val executionContext: ExecutionContext

  val animalController: AnimalController

  lazy val routes: Route =
    handleRequestTimeout {
      handleRejections(rejectionHandler) {
        handleExceptions(exceptionHandler) {
          new AnimalRoutes(animalController).animalRoutes
        }
      }
    }

}
