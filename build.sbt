lazy val dockerSettings = Seq(
  dockerBaseImage := "openjdk:jre-alpine",
  dockerImageCreationTask := (publishLocal in Docker).value
)

lazy val root =
  Project("akka-http-animal-service", file("."))
    .settings(
      name := "Akka Http Animal Service",
      organization := "com.example",
      scalaVersion := "2.12.4",
      resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
      libraryDependencies ++= Dependencies.dependencies,
      dependencyOverrides ++= Dependencies.dependencyOverrides,
      scalacOptions := ScalacOptions.default,
    )
    .settings(dockerSettings)
    .settings(Testing.e2eSettings)
    .configs(Testing.EndToEndTestConfig)
    .enablePlugins(JavaAppPackaging, DockerPlugin, AshScriptPlugin, DockerComposePlugin)
