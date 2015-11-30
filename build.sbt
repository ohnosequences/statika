Nice.scalaProject

name          := "statika"
organization  := "ohnosequences"
description   := "Managing dependencies"

bucketSuffix := "era7.com"
scalaVersion := "2.11.7"
crossScalaVersions := Seq("2.10.6", scalaVersion.value)

libraryDependencies ++= Seq (
  "org.scalatest" %% "scalatest" % "2.2.5" % Test
)

testOptions       in Test += Tests.Argument("-oD")
parallelExecution in Test := false
