package com.example.repository

import java.util.concurrent.TimeUnit

import com.example.config.AppConfig
import org.scalatest._

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext}

trait BaseRepositoryTest
    extends AsyncWordSpec
    with RepositoryModule
    with AppConfig
    with MustMatchers
    with OptionValues
    with BsonSupport
    with BeforeAndAfterEach
    with BeforeAndAfterAll {

  override implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global

  private val duration = Duration(3, TimeUnit.SECONDS)

  override protected def beforeEach(): Unit = {
    Await.ready(collection.flatMap(_.drop(failIfNotFound = false)), duration)
    ()
  }

  override protected def afterAll(): Unit = {
    Await.ready(collection.flatMap(_.drop(failIfNotFound = false)), duration)
    Await.ready(connection.map(_.askClose()(duration)), duration)
    ()
  }
}
