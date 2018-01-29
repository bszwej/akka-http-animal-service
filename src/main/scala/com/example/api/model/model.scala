package com.example.api.model

/**
  * Represents animal request body.
  *
  * @param name of an animal.
  * @param age  of an animal.
  * @param kind of an animal.
  */
final case class AnimalRequest(name: String, age: Int, kind: String)

/**
  * Represents animal response body.
  *
  * @param id   of an animal.
  * @param name of an animal.
  * @param age  of an animal.
  * @param kind of an animal.
  */
final case class AnimalResponse(id: String, name: String, age: Int, kind: String)

/**
  * Represents a response when animal is created.
  *
  * @param id of a newly created animal.
  */
final case class AnimalCreatedResponse(id: String)

/**
  * Represents additional request data.
  *
  * @param userAgent optional user agent client used to make request.
  * @param clientIP  optional ip address of client.
  */
final case class RequestMetadata(userAgent: Option[String], clientIP: String)
