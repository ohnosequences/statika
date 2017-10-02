name          := "statika"
organization  := "ohnosequences"
description   := "Managing dependencies"

bucketSuffix := "era7.com"

crossScalaVersions := Seq("2.11.11", "2.12.3")
scalaVersion  := crossScalaVersions.value.last

libraryDependencies ++= Seq (
  "ohnosequences" %% "aws-scala-tools" % "0.19.0",
  "org.scalatest" %% "scalatest"       % "3.0.4" % Test
)

testOptions       in Test += Tests.Argument("-oD")
parallelExecution in Test := false
