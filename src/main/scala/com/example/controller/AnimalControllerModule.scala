package com.example.controller

import com.example.repository.AnimalRepository

import scala.concurrent.ExecutionContext

trait AnimalControllerModule {

  implicit val executionContext: ExecutionContext
  lazy val animalController: AnimalController = new AnimalController(animalRepository)
  val animalRepository: AnimalRepository

}
