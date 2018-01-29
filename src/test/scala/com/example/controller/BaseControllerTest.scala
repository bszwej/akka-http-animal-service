package com.example.controller

import com.example.repository.AnimalRepository
import org.scalamock.scalatest.AsyncMockFactory
import org.scalatest.{AsyncWordSpec, EitherValues, MustMatchers}

import scala.concurrent.ExecutionContext

trait BaseControllerTest
    extends AsyncWordSpec
    with AsyncMockFactory
    with MustMatchers
    with EitherValues
    with AnimalControllerModule {

  override implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global
  override val animalRepository: AnimalRepository = mock[AnimalRepository]

}
