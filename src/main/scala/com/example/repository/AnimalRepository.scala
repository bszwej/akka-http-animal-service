package com.example.repository

import com.example.KamonSupport
import com.example.model.Animal
import reactivemongo.api.Cursor
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.bson.{BSONDocument, BSONObjectID}

import scala.concurrent.{ExecutionContext, Future}

object RepositorySuccess

class AnimalRepository(mongoCollection: Future[BSONCollection])(implicit ec: ExecutionContext) extends BsonSupport with KamonSupport {

  private val IdAttribute = "_id"
  private val FindLimit = 1000

  def create(animal: Animal): Future[String] = traceFuture("repository-insert-animal") {
    val objectId = BSONObjectID.generate
    val animalWithId = animal.copy(id = Some(objectId.stringify))

    for {
      mc ← mongoCollection
      _ ← mc.insert(animalWithId)
    } yield objectId.stringify
  }

  def find: Future[List[Animal]] = traceFuture("repository-get-animals") {
    val query = BSONDocument.empty

    for {
      mc ← mongoCollection
      result ← find(mc, query)
    } yield result
  }

  def get(id: String): Future[Option[Animal]] = traceFuture("repository-get-animal-by-id") {
    val query = BSONDocument(IdAttribute → id)

    for {
      mc ← mongoCollection
      result ← mc.find(query).one[Animal]
    } yield result.map(_.copy(id = Some(id)))
  }

  def delete(id: String): Future[RepositorySuccess.type] = traceFuture("repository-delete-animal") {
    val query = BSONDocument(IdAttribute → id)

    for {
      mc ← mongoCollection
      _ ← mc.remove(query)
    } yield RepositorySuccess
  }

  private def find(collection: BSONCollection, query: BSONDocument) =
    collection
      .find(query)
      .cursor[Animal]()
      .collect[List](FindLimit, Cursor.DoneOnError[List[Animal]]())

}
