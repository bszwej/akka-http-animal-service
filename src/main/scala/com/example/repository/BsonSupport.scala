package com.example.repository

import com.example.model.{Animal, Metadata}
import reactivemongo.bson.{BSONDocumentHandler, Macros}

trait BsonSupport {
  implicit val animalHandler: BSONDocumentHandler[Animal] = Macros.handler[Animal]
  implicit val animalMetadataHandler: BSONDocumentHandler[Metadata] = Macros.handler[Metadata]
}
