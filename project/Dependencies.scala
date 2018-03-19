import sbt._

object Version {

  val akkaHttp = "10.1.0"
  val akka = "2.5.11"
  val circe = "0.9.2"
  val akkaHttpCirce = "1.20.0"
  val reactiveMongo = "0.13.0"
  val logback = "1.2.3"
  val kamon = "1.1.0"
  val kamonJaeger = "1.0.1"

  val sttp = "1.1.10"
  val scalaTest = "3.0.5"
  val scalaMock = "4.2.0-SNAPSHOT"
}

object Dependencies {

  private lazy val compileDependencies = Seq(
    "com.typesafe.akka" %% "akka-http" % Version.akkaHttp,
    "io.circe" %% "circe-core" % Version.circe,
    "io.circe" %% "circe-generic" % Version.circe,
    "io.circe" %% "circe-parser" % Version.circe,
    "de.heikoseeberger" %% "akka-http-circe" % Version.akkaHttpCirce,
    "com.typesafe.akka" %% "akka-stream" % Version.akka,
    "com.typesafe.akka" %% "akka-slf4j" % Version.akka,
    "ch.qos.logback" % "logback-classic" % Version.logback,
    "org.reactivemongo" %% "reactivemongo" % Version.reactiveMongo,
    "io.kamon" %% "kamon-core" % Version.kamon,
    "io.kamon" %% "kamon-akka-http-2.5" % Version.kamon,
    "io.kamon" %% "kamon-jaeger" % Version.kamonJaeger
  )

  private lazy val testDependencies = Seq(
    "com.softwaremill.sttp" %% "core" % Version.sttp % Testing.EndToEndTestConfig,
    "com.softwaremill.sttp" %% "circe" % Version.sttp % Testing.EndToEndTestConfig,
    "com.typesafe.akka" %% "akka-http-testkit" % Version.akkaHttp % Test,
    "org.scalatest" %% "scalatest" % Version.scalaTest % Test,
    "org.scalamock" %% "scalamock" % Version.scalaMock % Test
  )

  lazy val dependencies = compileDependencies ++ testDependencies

  lazy val dependencyOverrides =
    Seq(
      "com.typesafe.akka" %% "akka-actor" % Version.akka,
      "com.typesafe.akka" %% "akka-http" % Version.akkaHttp,
      "com.typesafe.akka" %% "akka-stream" % Version.akka,
      "io.kamon" %% "kamon-core" % Version.kamon
    )
}
