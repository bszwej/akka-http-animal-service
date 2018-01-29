package com.example.repository

import com.example.config.AppConfig
import reactivemongo.api.{MongoConnection, MongoDriver}
import reactivemongo.api.collections.bson.BSONCollection

import scala.concurrent.{ExecutionContext, Future}

trait RepositoryModule {
  this: AppConfig ⇒

  implicit val executionContext: ExecutionContext

  lazy val animalRepository: AnimalRepository = new AnimalRepository(collection)

  protected lazy val connection: Future[MongoConnection] =
    Future.fromTry(MongoDriver().connection(mongoUri))

  protected lazy val collection: Future[BSONCollection] =
    for {
      conn <- connection
      db ← conn.database(dbName)
    } yield db.collection(collectionName)

}
