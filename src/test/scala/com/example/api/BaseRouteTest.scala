package com.example.api

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.example.api.model.{AnimalCreatedResponse, AnimalRequest, AnimalResponse, ErrorResponse}
import com.example.controller.AnimalController
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}
import org.scalamock.scalatest.MockFactory
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{MustMatchers, OneInstancePerTest, WordSpec}

import scala.concurrent.ExecutionContext

trait BaseRouteTest
    extends WordSpec
    with MustMatchers
    with ScalaFutures
    with ScalatestRouteTest
    with MockFactory
    with OneInstancePerTest
    with JsonSupport {

  implicit val animalCreatedResponseDecoder: Decoder[AnimalCreatedResponse] = deriveDecoder
  implicit val animalRequestEncoder: Encoder[AnimalRequest] = deriveEncoder
  implicit val animalResponseDecoder: Decoder[AnimalResponse] = deriveDecoder
  implicit val errorResponseDecoder: Decoder[ErrorResponse] = deriveDecoder

  val animalControllerMock: AnimalController = mock[AnimalController]

  private val api = new Api {
    override implicit val executionContext: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global
    override val animalController: AnimalController = animalControllerMock
  }

  val routes: Route = api.routes
}
