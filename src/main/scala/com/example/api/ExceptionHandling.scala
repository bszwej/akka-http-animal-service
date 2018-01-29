package com.example.api

import akka.event.slf4j.SLF4JLogging
import akka.http.scaladsl.model.{IllegalRequestException, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.ExceptionHandler
import com.example.api.model.ErrorResponse

import scala.util.control.NonFatal

trait ExceptionHandling extends SLF4JLogging {
  this: JsonSupport ⇒

  lazy val exceptionHandler: ExceptionHandler = ExceptionHandler {
    case IllegalRequestException(info, status) ⇒
      log.error("Exception caught in handler: '{}'", info.detail)
      complete((status, ErrorResponse(info.summary)))

    case NonFatal(e) =>
      log.error("Exception caught in handler: '{}'", e.getMessage)
      complete((StatusCodes.InternalServerError, ErrorResponse("Internal server error occurred.")))
  }

}
