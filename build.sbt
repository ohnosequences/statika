Nice.scalaProject

name          := "statika"
organization  := "ohnosequences"
description   := "Managing dependencies in compile time"

bucketSuffix := "era7.com"
scalaVersion := "2.11.6"
crossScalaVersions := Seq("2.10.5", scalaVersion.value)

libraryDependencies ++= Seq (
  "ohnosequences" %% "cosas"     % "0.6.0",
  "org.scalatest" %% "scalatest" % "2.2.4" % Test
)

testOptions       in Test += Tests.Argument("-oD")
parallelExecution in Test := false

incOptions := incOptions.value.withNameHashing(false)
