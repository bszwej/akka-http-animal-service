package com.example.model

import reactivemongo.bson.Macros.Annotations.Key

/**
  * Represents an animal.
  *
  * @param id       of an animal.
  * @param name     of an animal.
  * @param age      of an animal.
  * @param kind     of an animal.
  * @param metadata of an animal.
  */
final case class Animal(@Key("_id") id: Option[String], name: String, age: Int, kind: String, metadata: Metadata)

/**
  * Represents an additional data stored along with an animal.
  *
  * @param userAgent user agent, that client used to create an animal.
  * @param clientIp  ip address of a client, that created an animal.
  */
final case class Metadata(userAgent: Option[String], clientIp: String)
