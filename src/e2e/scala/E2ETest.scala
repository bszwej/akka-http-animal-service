import com.softwaremill.sttp._
import com.softwaremill.sttp.circe._
import io.circe.generic.auto._
import io.circe.parser.decode

class E2ETest extends BaseE2ETest {

  "Microservice" should {
    "insert and get animal by id" in {
      // given
      val insertAnimalResponse = insertAnimal(AnimalRequest("e2e-kitten", 22, "cat"))
      insertAnimalResponse.code mustBe 201

      val animalId = insertAnimalResponse.body.right.value.id

      // when
      val animalResponse = getAnimalById(animalId)

      // then
      animalResponse.code mustBe 200
      animalResponse.body.right.value mustBe AnimalResponse(animalId, "e2e-kitten", 22, "cat")

      // cleanup
      deleteAnimal(animalId)
    }

    "handle wrong payload when inserting" in {
      // when
      val insertAnimalResponse = insertAnimal("""{"wrong": "payload"}""")

      // then
      insertAnimalResponse.code mustBe 400
      insertAnimalResponse.body.left.value.message must not be empty
    }

    "handle malformed payload when inserting" in {
      // when
      val insertAnimalResponse = insertAnimal("""{"malformed": "payl""")

      // then
      insertAnimalResponse.code mustBe 400
      insertAnimalResponse.body.left.value.message must not be empty
    }

    "handle empty payload when inserting" in {
      // when
      val insertAnimalResponse = insertAnimal("")

      // then
      insertAnimalResponse.code mustBe 400
      insertAnimalResponse.body.left.value.message must not be empty
    }

    "return 404 if not found when getting by id" in {
      // when
      val deleteResponse = getAnimalById("non-existent-id")

      // then
      deleteResponse.code mustBe 404
      deleteResponse.body.left.value.message mustBe "Animal with id 'non-existent-id' not found."
    }

    "delete an animal" in {
      // given
      val insertAnimalResponse = insertAnimal(AnimalRequest("e2e-t rex", 10, "dinosaur"))
      val animalId = insertAnimalResponse.body.right.value.id

      getAnimalById(animalId).code mustBe 200

      // when
      val deleteResponse = deleteAnimal(animalId)

      // then
      deleteResponse.code mustBe 204
      deleteResponse.body mustBe empty

      getAnimalById(animalId).code mustBe 404
    }

    "return 204 if not found when deleting" in {
      // when
      val deleteResponse = deleteAnimal("non-existent-id")

      // then
      deleteResponse.code mustBe 204
      deleteResponse.body mustBe empty
    }

    "list animals" in {
      // given
      val animalId1 = insertAnimal(AnimalRequest("e2e-kitten", 22, "cat")).body.right.value.id
      val animalId2 = insertAnimal(AnimalRequest("e2e-t rex", 10, "dinosaur")).body.right.value.id

      // when
      val animalsResponse = getAnimals

      // then
      animalsResponse.code mustBe 200
      animalsResponse.body must contain only
        (AnimalResponse(animalId1, "e2e-kitten", 22, "cat"), AnimalResponse(animalId2, "e2e-t rex", 10, "dinosaur"))

      //cleanup
      deleteAnimal(animalId1)
      deleteAnimal(animalId2)
    }

    "return empty list if no animals found" in {
      // when
      val animalsResponse = getAnimals

      // then
      animalsResponse.code mustBe 200
      animalsResponse.body mustBe empty
    }

    "return 404 when wrong route is called" in {
      // when
      val response =
        sttp
          .get(uri"$url/non/existing/route")
          .send()

      // then
      response.code mustBe 404

      val responseBody = response.body.left.value
      val errorResponse = decode[ErrorResponse](responseBody).right.value
      errorResponse.message mustBe "The requested resource could not be found."
    }

  }

}
