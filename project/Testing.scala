import sbt.Keys._
import sbt._

object Testing {

  lazy val EndToEndTestConfig = config("e2e") extend (Test)

  lazy val e2eSettings =
    inConfig(EndToEndTestConfig)(Defaults.testSettings) ++
      Seq(scalaSource in EndToEndTestConfig := baseDirectory.value / "src/e2e/scala")

}
