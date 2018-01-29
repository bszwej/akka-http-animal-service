case class IdResponse(id: String)

case class ErrorResponse(message: String)

case class AnimalResponse(id: String, name: String, age: Int, kind: String)

case class AnimalRequest(name: String, age: Int, kind: String)

case class Response[T](code: Int, body: T)
