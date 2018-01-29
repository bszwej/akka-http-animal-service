package com.example.api

import akka.event.slf4j.SLF4JLogging
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse}
import akka.http.scaladsl.server.RejectionHandler
import com.example.api.model.ErrorResponse
import io.circe.syntax._

trait RejectionHandling extends SLF4JLogging {
  this: JsonSupport ⇒

  def rejectionHandler: RejectionHandler =
    RejectionHandler.default
      .mapRejectionResponse {

        case res @ HttpResponse(_, _, ent: HttpEntity.Strict, _) =>
          val message = ent.data.utf8String.replaceAll("\"", """\"""")
          res.copy(entity = HttpEntity(ContentTypes.`application/json`, ErrorResponse(message).asJson.noSpaces))

        case x => x
      }
}
