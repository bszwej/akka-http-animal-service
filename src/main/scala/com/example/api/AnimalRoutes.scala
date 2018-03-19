package com.example.api

import akka.http.scaladsl.model.RemoteAddress
import akka.http.scaladsl.model.headers.`User-Agent`
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.MethodDirectives.{delete, get, post}
import akka.http.scaladsl.server.directives.PathDirectives.path
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import com.example.api.model.{AnimalRequest, RequestMetadata}
import com.example.controller.AnimalController
import kamon.akka.http.TracingDirectives

class AnimalRoutes(animalController: AnimalController) extends JsonSupport with TracingDirectives {

  lazy val animalRoutes: Route =
    pathPrefix("animals") {
      pathEnd {
        get {
          operationName("get-animals") {
            complete(animalController.findAll.value)
          }
        } ~
          post {
            entity(as[AnimalRequest]) { animalRequest =>
              optionalHeaderValueByType[`User-Agent`](()) { userAgent ⇒
                extractClientIP { remoteAddress ⇒
                  operationName("insert-animal") {
                    val requestMetadata = prepareRequestMetadata(userAgent, remoteAddress)
                    complete(animalController.create(animalRequest, requestMetadata).value)
                  }
                }
              }
            }
          }
      } ~
        path(Segment) { id =>
          get {
            operationName("get-animal-by-id") {
              complete(animalController.getById(id).value)
            }
          } ~
            delete {
              operationName("delete-animal") {
                complete(animalController.delete(id).value)
              }
            }
        }
    }

  private def prepareRequestMetadata(userAgent: Option[`User-Agent`], remoteAddress: RemoteAddress) =
    RequestMetadata(
      userAgent = userAgent.map(_.value),
      clientIP = remoteAddress.toIP.map(_.ip.getHostAddress).getOrElse("unknown")
    )

}
