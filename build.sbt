Nice.scalaProject

name          := "statika"
organization  := "ohnosequences"
description   := "Managing dependencies"

bucketSuffix := "era7.com"

scalaVersion := "2.11.7"

libraryDependencies ++= Seq (
  "ohnosequences" %% "aws-scala-tools" % "0.16.0",
  "org.scalatest" %% "scalatest" % "2.2.5" % Test
)

testOptions       in Test += Tests.Argument("-oD")
parallelExecution in Test := false
