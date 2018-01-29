import com.softwaremill.sttp._
import com.softwaremill.sttp.circe.asJson
import io.circe.generic.auto._
import io.circe.parser.decode
import org.scalatest.{EitherValues, MustMatchers, OptionValues, WordSpec}

trait BaseE2ETest extends WordSpec with MustMatchers with EitherValues with OptionValues {

  implicit val backend: SttpBackend[Id, Nothing] = HttpURLConnectionBackend()
  val url: String = sys.props.getOrElse("url", "http://localhost:8080")

  def insertAnimal[T: BodySerializer](animalRequest: T): Response[Either[ErrorResponse, IdResponse]] = {
    val response = sttp
      .post(uri"$url/animals")
      .contentType("application/json")
      .body(animalRequest)
      .send()

    response.body.fold(
      errorResponse ⇒ Response(response.code, Left(decode[ErrorResponse](errorResponse).right.value)),
      idResponse ⇒ Response(response.code, Right(decode[IdResponse](idResponse).right.value))
    )
  }

  def getAnimalById(id: String): Response[Either[ErrorResponse, AnimalResponse]] = {
    val response =
      sttp
        .get(uri"$url/animals/$id")
        .send()

    response.body.fold(
      errorResponse ⇒ Response(response.code, Left(decode[ErrorResponse](errorResponse).right.value)),
      animalResponse ⇒ Response(response.code, Right(decode[AnimalResponse](animalResponse).right.value))
    )
  }

  def getAnimals: Response[List[AnimalResponse]] = {
    val response =
      sttp
        .get(uri"$url/animals")
        .response(asJson[List[AnimalResponse]])
        .send()

    val animalResponseBody = response.body.right.value
    Response(response.code, animalResponseBody.right.value)
  }

  def deleteAnimal(animalId: String): Response[String] = {
    val response = sttp.delete(uri"$url/animals/$animalId").response(asString).send()
    Response(response.code, response.body.right.value)
  }

}
