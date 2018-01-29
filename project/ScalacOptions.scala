object ScalacOptions {

  lazy val default = Seq(
    "-encoding",
    "utf-8",
    "-explaintypes",
    "-unchecked",
    "-deprecation",
    "-feature",
    "-Xlint:_",
    "-Ywarn-unused:_",
    "-Yno-adapted-args",
    "-Ywarn-dead-code",
    "-Ywarn-inaccessible",
    "-Ywarn-nullary-override",
    "-Ywarn-nullary-unit",
    "-Ywarn-numeric-widen",
    "-Ywarn-value-discard",
    "-Ywarn-infer-any"
  )

}
