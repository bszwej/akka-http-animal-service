package com.example.api

import akka.http.scaladsl.marshalling.{Marshaller, ToResponseMarshaller}
import akka.http.scaladsl.model.MediaTypes.`application/json`
import akka.http.scaladsl.model._
import com.example.api.model.{AnimalCreatedResponse, AnimalRequest, AnimalResponse, ErrorResponse}
import com.example.controller.AnimalDeleted
import com.example.model.{InternalServiceError, NotFoundError, ServiceError}
import de.heikoseeberger.akkahttpcirce.ErrorAccumulatingCirceSupport
import io.circe.generic.semiauto._
import io.circe.syntax._
import io.circe.{Decoder, Encoder, Printer}

trait JsonSupport extends ErrorAccumulatingCirceSupport {

  implicit val printer: Printer = Printer.noSpaces

  implicit val animalRequestDecoder: Decoder[AnimalRequest] = deriveDecoder
  implicit val animalResponseEncoder: Encoder[AnimalResponse] = deriveEncoder

  implicit val animalCreatedResponseEncoder: Encoder[AnimalCreatedResponse] = deriveEncoder

  implicit val errorResponseEncoder: Encoder[ErrorResponse] = deriveEncoder

  implicit val animalCreatedResponseMarshaller: ToResponseMarshaller[AnimalCreatedResponse] = {
    Marshaller.withFixedContentType(MediaTypes.`application/json`) { acr =>
      HttpResponse(
        status = StatusCodes.Created,
        entity = HttpEntity(`application/json`, printer.pretty(acr.asJson))
      )
    }
  }

  implicit val animalDeletedMarshaller: ToResponseMarshaller[AnimalDeleted.type] =
    Marshaller.withFixedContentType(MediaTypes.`application/json`) { _ =>
      HttpResponse(
        status = StatusCodes.NoContent,
        entity = HttpEntity.empty(ContentTypes.`application/json`)
      )
    }

  implicit val errorMarshaller: ToResponseMarshaller[ServiceError] = Marshaller.combined {
    case InternalServiceError(msg) ⇒
      (StatusCodes.InternalServerError, ErrorResponse(msg))
    case NotFoundError(msg) ⇒
      (StatusCodes.NotFound, ErrorResponse(msg))
  }

}
