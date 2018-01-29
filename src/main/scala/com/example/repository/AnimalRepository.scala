package com.example.repository

import com.example.model.{Animal, Metadata}
import reactivemongo.api.Cursor
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.bson.{BSONDocument, BSONDocumentHandler, BSONObjectID, Macros}

import scala.concurrent.{ExecutionContext, Future}

trait BsonSupport {
  implicit val animalHandler: BSONDocumentHandler[Animal] = Macros.handler[Animal]
  implicit val animalMetadataHandler: BSONDocumentHandler[Metadata] = Macros.handler[Metadata]
}

object RepositorySuccess

class AnimalRepository(mongoCollection: Future[BSONCollection])(implicit ec: ExecutionContext) extends BsonSupport {

  private val IdAttribute = "_id"
  private val FindLimit = 1000

  def create(animal: Animal): Future[String] = {
    // TODO: persist Mongo's ObjectID instead of string hex representation.
    val objectId = BSONObjectID.generate
    val animalWithId = animal.copy(id = Some(objectId.stringify))

    for {
      mc ← mongoCollection
      _ ← mc.insert(animalWithId)
    } yield objectId.stringify
  }

  def find: Future[List[Animal]] = {
    val query = BSONDocument.empty
    for {
      mc ← mongoCollection
      result ← find(mc, query)
    } yield result
  }

  def get(id: String): Future[Option[Animal]] =
    for {
      mc ← mongoCollection
      query = BSONDocument(IdAttribute → id)
      result ← mc.find(query).one[Animal]
    } yield result.map(_.copy(id = Some(id)))

  def delete(id: String): Future[RepositorySuccess.type] =
    for {
      mc ← mongoCollection
      query = BSONDocument(IdAttribute → id)
      _ ← mc.remove(query)
    } yield RepositorySuccess

  private def find(collection: BSONCollection, query: BSONDocument) =
    collection
      .find(query)
      .cursor[Animal]()
      .collect[List](FindLimit, Cursor.DoneOnError[List[Animal]]())

}
