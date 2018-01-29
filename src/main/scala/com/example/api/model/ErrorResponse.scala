package com.example.api.model

/**
  * Represents an error message sent to client in response body.
  *
  * @param message to be shown to client.
  */
final case class ErrorResponse(message: String)
