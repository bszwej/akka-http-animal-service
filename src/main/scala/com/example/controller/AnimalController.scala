package com.example.controller

import akka.event.slf4j.SLF4JLogging
import com.example.api.model.{AnimalCreatedResponse, AnimalRequest, AnimalResponse, RequestMetadata}
import com.example.model.AsyncResult.AsyncResult
import com.example.model._
import com.example.repository.AnimalRepository

import scala.concurrent.ExecutionContext
import scala.util.control.NonFatal

case object AnimalDeleted

class AnimalController(animalRepository: AnimalRepository)(implicit ec: ExecutionContext) extends SLF4JLogging {

  def create(animalRequest: AnimalRequest, requestMetadata: RequestMetadata): AsyncResult[AnimalCreatedResponse] =
    AsyncResult {
      val animal = animalRequestToAnimal(animalRequest, requestMetadata.userAgent, requestMetadata.clientIP)

      animalRepository
        .create(animal)
        .map(id ⇒ Right(AnimalCreatedResponse(id)))
        .recover {
          case NonFatal(e) ⇒
            log.error("Error creating animal: '{}'", e.getMessage)
            Left(InternalServiceError("Internal error when creating animal."))
        }
    }

  def findAll: AsyncResult[List[AnimalResponse]] =
    AsyncResult {
      animalRepository.find
        .map(toListOfAnimalResponse)
        .map(Right(_))
        .recover {
          case NonFatal(e) ⇒
            log.error("Error finding animals: '{}'", e.getMessage)
            Left(InternalServiceError("Internal error when finding animals."))
        }
    }

  def getById(id: String): AsyncResult[AnimalResponse] =
    AsyncResult {
      animalRepository
        .get(id)
        .map {
          case Some(animal) ⇒
            Right(animalToAnimalResponse(animal))
          case None ⇒
            Left(NotFoundError(s"Animal with id '$id' not found."))
        }
        .recover {
          case NonFatal(e) ⇒
            log.error("Error getting animal by id '{}'.", e.getMessage)
            Left(InternalServiceError("Internal error when getting animal by id."))
        }
    }

  def delete(id: String): AsyncResult[AnimalDeleted.type] =
    AsyncResult {
      animalRepository
        .delete(id)
        .map(_ ⇒ Right(AnimalDeleted))
        .recover {
          case NonFatal(e) ⇒
            log.error("Error deleting animal '{}'.", e.getMessage)
            Left(InternalServiceError("Internal error when deleting animal."))
        }
    }

  private val toListOfAnimalResponse: List[Animal] ⇒ List[AnimalResponse] =
    listOfAnimals ⇒ listOfAnimals.map(animalToAnimalResponse)

  private def animalRequestToAnimal(animalRequest: AnimalRequest, userAgent: Option[String], ip: String): Animal =
    Animal(
      id = None,
      name = animalRequest.name,
      age = animalRequest.age,
      kind = animalRequest.kind,
      metadata = Metadata(userAgent, ip))

  private def animalToAnimalResponse(animal: Animal): AnimalResponse =
    AnimalResponse(
      id = animal.id.getOrElse("unknown"),
      name = animal.name,
      age = animal.age,
      kind = animal.kind
    )

}
