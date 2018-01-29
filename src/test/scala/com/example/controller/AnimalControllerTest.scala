package com.example.controller

import com.example.api.model.{AnimalCreatedResponse, AnimalRequest, AnimalResponse, RequestMetadata}
import com.example.model.{Animal, InternalServiceError, Metadata, NotFoundError}
import com.example.repository.RepositorySuccess

import scala.concurrent.Future

class AnimalControllerTest extends BaseControllerTest {

  "AnimalController" when {
    "creating" should {
      "create an animal" in {
        // given
        val animal = Animal(None, "Charlie", 20, "unicorn", Metadata(Some("curl"), "52.22.43.14"))

        val repositoryResult = Future.successful("abcd")
        (animalRepository.create _).expects(animal).returning(repositoryResult)

        val animalRequest = AnimalRequest("Charlie", 20, "unicorn")
        val requestMetadata = RequestMetadata(Some("curl"), "52.22.43.14")

        // when
        val result = animalController.create(animalRequest, requestMetadata).value

        // then
        result.map { r ⇒
          r mustBe Right(AnimalCreatedResponse("abcd"))
        }
      }

      "handle repository errors" in {
        // given
        val repositoryResult = Future.failed(new RuntimeException("DB not reachable"))
        (animalRepository.create _).expects(*).returning(repositoryResult)

        val animalRequest = AnimalRequest("Charlie", 20, "unicorn")
        val requestMetadata = RequestMetadata(None, "")

        // when
        val result = animalController.create(animalRequest, requestMetadata).value

        // then
        result.map { r ⇒
          r.left.value mustBe a[InternalServiceError]
          r.left.value.message must not be empty
        }
      }
    }

    "finding all" should {
      "find all animals" in {
        // given
        val animal = Animal(Some("1"), "Charlie", 20, "unicorn", Metadata(Some("curl"), ""))

        val repositoryResult = Future.successful(List(animal))
        (animalRepository.find _).expects().returning(repositoryResult)

        // when
        val result = animalController.findAll.value

        // then
        result.map { r ⇒
          r mustBe Right(List(AnimalResponse("1", "Charlie", 20, "unicorn")))
        }
      }

      "handle repository errors" in {
        // given
        val repositoryResult = Future.failed(new RuntimeException("DB not reachable"))
        (animalRepository.find _).expects().returning(repositoryResult)

        // when
        val result = animalController.findAll.value

        // then
        result.map { r ⇒
          r.left.value mustBe a[InternalServiceError]
          r.left.value.message must not be empty
        }
      }
    }

    "getting by id" should {
      "get animal by id" in {
        // given
        val animal = Animal(Some("1"), "Charlie", 20, "unicorn", Metadata(Some("curl"), ""))

        val repositoryResult = Future.successful(Some(animal))
        (animalRepository.get _).expects("1").returning(repositoryResult)

        // when
        val result = animalController.getById("1").value

        // then
        result.map { r ⇒
          r mustBe Right(AnimalResponse("1", "Charlie", 20, "unicorn"))
        }
      }

      "return not found" in {
        // given
        val repositoryResult = Future.successful(None)
        (animalRepository.get _).expects("1").returning(repositoryResult)

        // when
        val result = animalController.getById("1").value

        // then
        result.map { r ⇒
          r.left.value mustBe a[NotFoundError]
          r.left.value.message must not be empty
        }
      }

      "handle repository errors" in {
        // given
        val repositoryResult = Future.failed(new RuntimeException("DB not reachable"))
        (animalRepository.get _).expects(*).returning(repositoryResult)

        // when
        val result = animalController.getById("1").value

        // then
        result.map { r ⇒
          r.left.value mustBe a[InternalServiceError]
          r.left.value.message must not be empty
        }
      }
    }

    "deleting" should {
      "delete an animal" in {
        // given
        val repositoryResult = Future.successful(RepositorySuccess)
        (animalRepository.delete _).expects("1").returning(repositoryResult)

        // when
        val result = animalController.delete("1").value

        // then
        result.map { r ⇒
          r mustBe Right(AnimalDeleted)
        }
      }

      "handle repository errors" in {
        // given
        val repositoryResult = Future.failed(new RuntimeException("DB not reachable"))
        (animalRepository.delete _).expects(*).returning(repositoryResult)

        // when
        val result = animalController.delete("1").value

        // then
        result.map { r ⇒
          r.left.value mustBe a[InternalServiceError]
          r.left.value.message must not be empty
        }
      }
    }
  }
}
