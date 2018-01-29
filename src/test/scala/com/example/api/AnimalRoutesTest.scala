package com.example.api

import java.net.InetAddress

import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.`Remote-Address`
import com.example.api.model._
import com.example.controller.AnimalDeleted
import com.example.model._

class AnimalRoutesTest extends BaseRouteTest {

  "AnimalRoutes" when {
    "GET /animals is requested" should {
      "return a list with animals" in {
        // given
        val request = Get("/animals")

        val animalResponse = AnimalResponse("abcd", "Charlie", 20, "unicorn")
        val controllerResult = AsyncResult.success(List(animalResponse))
        (animalControllerMock.findAll _).expects().returning(controllerResult)

        // when
        request ~> routes ~> check {

          // then
          status mustBe StatusCodes.OK
          contentType mustBe ContentTypes.`application/json`
          entityAs[List[AnimalResponse]] mustBe List(AnimalResponse("abcd", "Charlie", 20, "unicorn"))
        }
      }

      "return an empty list when no animals found" in {
        // given
        val request = Get("/animals")

        val controllerResult = AsyncResult.success(List.empty[AnimalResponse])
        (animalControllerMock.findAll _).expects().returning(controllerResult)

        // when
        request ~> routes ~> check {

          // then
          status mustBe StatusCodes.OK
          entityAs[List[AnimalResponse]] mustBe Nil
        }
      }

      "handle controller errors" in {
        // given
        val controllerResult = AsyncResult.failure[List[AnimalResponse]](InternalServiceError("DB not reachable."))
        (animalControllerMock.findAll _).expects().returning(controllerResult)

        val request = Get("/animals")

        // when
        request ~> routes ~> check {

          // then
          status mustBe StatusCodes.InternalServerError
          entityAs[ErrorResponse].message must not be empty
        }
      }
    }

    "GET /animals/{id} is requested" should {

      "return a single animal" in {
        // given
        val request = Get("/animals/1")

        val animal = AnimalResponse("abcd", "Charlie", 20, "unicorn")
        val controllerResult = AsyncResult.success(animal)
        (animalControllerMock.getById _).expects("1").returning(controllerResult)

        // when
        request ~> routes ~> check {

          // then
          status mustBe StatusCodes.OK
          contentType mustBe ContentTypes.`application/json`
          entityAs[AnimalResponse] mustBe AnimalResponse("abcd", "Charlie", 20, "unicorn")
        }
      }

      "return 404 when animal not found" in {
        // given
        val request = Get("/animals/1")

        val controllerResult = AsyncResult.failure[AnimalResponse](NotFoundError("Animal not found."))
        (animalControllerMock.getById _).expects("1").returning(controllerResult)

        // when
        request ~> routes ~> check {

          // then
          entityAs[ErrorResponse].message must not be empty
          status mustBe StatusCodes.NotFound
          contentType mustBe ContentTypes.`application/json`
        }
      }

      "handle repository errors" in {
        // given
        val controllerResult = AsyncResult.failure[AnimalResponse](InternalServiceError("DB not reachable"))
        (animalControllerMock.getById _).expects(*).returning(controllerResult)

        val request = Get("/animals/1")

        // when
        request ~> routes ~> check {

          // then
          status mustBe StatusCodes.InternalServerError
          entityAs[ErrorResponse].message must not be empty
        }
      }
    }

    "POST /animals is requested" should {

      "create an animal" in {
        // given
        val request =
          Post("/animals", AnimalRequest("Charlie", 20, "unicorn"))
            .withHeaders(`Remote-Address`(RemoteAddress(InetAddress.getByName("127.0.0.1"))))

        val animalRequest = AnimalRequest("Charlie", 20, "unicorn")
        val requestMetadata = RequestMetadata(Some("curl"), "127.0.0.1")

        val controllerResult = AsyncResult.success(AnimalCreatedResponse("abcd"))
        (animalControllerMock.create _).expects(animalRequest, requestMetadata).returning(controllerResult)

        // when
        request ~> addHeader("User-Agent", "curl") ~> routes ~> check {

          // then
          status mustBe StatusCodes.Created
          entityAs[AnimalCreatedResponse] mustBe AnimalCreatedResponse("abcd")
        }
      }

      "handle wrong request body" in {
        // given
        val wrongRequestBody =
          """{"wrong": "body"}"""
        val request = Post("/animals", HttpEntity(ContentTypes.`application/json`, wrongRequestBody))

        (animalControllerMock.create _).expects(*, *).never()

        // when
        request ~> routes ~> check {

          // then
          status mustBe StatusCodes.BadRequest
          entityAs[ErrorResponse].message must not be empty
        }
      }

      "handle repository errors" in {
        // given
        val controllerResult = AsyncResult.failure[AnimalCreatedResponse](InternalServiceError("DB not reachable"))
        (animalControllerMock.create _).expects(*, *).returning(controllerResult)

        val request = Post("/animals", AnimalRequest("Charlie", 20, "unicorn"))

        // when
        request ~> routes ~> check {

          // then
          status mustBe StatusCodes.InternalServerError
          entityAs[ErrorResponse].message must not be empty
        }
      }
    }

    "DELETE /animals is requested" should {

      "delete an animal" in {
        // given
        val request = Delete("/animals/1")

        val controllerResult = AsyncResult.success(AnimalDeleted)
        (animalControllerMock.delete _).expects("1").returning(controllerResult)

        // when
        request ~> routes ~> check {

          // then
          status mustBe StatusCodes.NoContent
          responseAs[Option[String]] mustBe None
        }
      }

      "handle repository errors" in {
        // given
        val controllerResult = AsyncResult.failure[AnimalDeleted.type](InternalServiceError("DB not reachable"))
        (animalControllerMock.delete _).expects("1").returning(controllerResult)

        val request = Delete("/animals/1")

        // when
        request ~> routes ~> check {

          // then
          status mustBe StatusCodes.InternalServerError
          entityAs[ErrorResponse].message must not be empty
        }
      }

    }
  }
}
