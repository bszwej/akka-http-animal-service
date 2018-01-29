package com.example.api

import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directive0
import akka.http.scaladsl.server.Directives.withRequestTimeoutResponse
import com.example.api.model.ErrorResponse
import io.circe.syntax._

trait Directives {
  this: JsonSupport ⇒

  private val timeoutResponseEntity =
    HttpEntity(
      contentType = ContentTypes.`application/json`,
      string = ErrorResponse("Request timed out because of internal server error.").asJson.noSpaces
    )

  private val timeoutResponse =
    HttpResponse(
      status = StatusCodes.InternalServerError,
      entity = timeoutResponseEntity
    )

  val handleRequestTimeout: Directive0 = withRequestTimeoutResponse(_ => timeoutResponse)

}
