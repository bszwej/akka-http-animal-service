package com.example.model

/**
  * A sum type of all failed results of requests.
  *
  * @param message to be shown to the user.
  */
sealed abstract class ServiceError(val message: String)

/**
  * Represents an internal error. Example: database not available.
  *
  * @param message to be shown to the user.
  */
final case class InternalServiceError(override val message: String) extends ServiceError(message)

/**
  * Represents a resource not found error.
  *
  * @param message to be shown to the user.
  */
final case class NotFoundError(override val message: String) extends ServiceError(message)
