package com.example.repository

import com.example.model.{Animal, Metadata}
import reactivemongo.bson.BSONDocument
import tags.RequiresDb

@RequiresDb
class AnimalRepositoryTest extends BaseRepositoryTest {

  override val dbName = "animalrepository_test_db"
  override val collectionName = "animalrepository_test_col"

  "AnimalRepository" when {
    "creating" should {
      "create an animal" in {
        // given
        val animal = Animal(None, "doggy dog", 4, "dog", Metadata(Some("chrome"), "127.0.0.1"))

        // when
        for {
          id ← animalRepository.create(animal)

          query = BSONDocument("_id" → id, "name" → "doggy dog", "age" → 4, "kind" → "dog")
          result ← collection.flatMap(_.find(query).one[Animal])
        } yield {

          // then
          result mustBe Some(animal.copy(id = Some(id)))
        }
      }
    }

    "finding" should {
      "find animals" in {
        // given
        val a1 = Animal(None, "doggy dog", 4, "dog", Metadata(None, ""))
        val a2 = Animal(None, "kitten", 1, "cat", Metadata(None, ""))
        val a3 = Animal(None, "Charlie", 20, "unicorn", Metadata(None, ""))

        // when
        for {
          _ ← collection.flatMap(_.insert(a1))
          _ ← collection.flatMap(_.insert(a2))
          _ ← collection.flatMap(_.insert(a3))
          result ← animalRepository.find
        } yield {

          // then
          result must contain only (a1, a2, a3)
        }
      }

      "return empty list if no animals" in {
        // when
        animalRepository.find.map { result ⇒
          // then
          result mustBe empty
        }
      }
    }

    "getting by id" should {
      "get animal by id" in {
        // given
        val animal = Animal(None, "dog", 4, "dog", Metadata(None, ""))

        // when
        for {
          id ← animalRepository.create(animal)
          result ← animalRepository.get(id)
        } yield {

          // then
          result mustBe Some(animal.copy(id = Some(id)))
        }
      }

      "return None if animal not found" in {
        // given
        val nonExistingId = "5a77488f0100000100a45ca9"

        // when
        animalRepository.get(nonExistingId).map { result ⇒
          // then
          result mustBe None
        }
      }
    }

    "deleting" should {
      "delete by id" in {
        // given
        val animal = Animal(None, "dog", 4, "dog", Metadata(None, ""))

        // when
        for {
          id ← animalRepository.create(animal)
          _ ← animalRepository.delete(id)
          getByIdResult ← animalRepository.get(id)
        } yield {

          // then
          getByIdResult mustBe None
        }
      }
    }
  }
}
