name          := "statika"
organization  := "ohnosequences"
description   := "Managing dependencies"

bucketSuffix := "era7.com"

crossScalaVersions := Seq("2.11.11", "2.12.3")
scalaVersion  := crossScalaVersions.value.last

libraryDependencies ++= Seq (
  "ohnosequences" %% "aws-scala-tools" % "0.19.0"
)

testOptions       in Test += Tests.Argument("-oD")
parallelExecution in Test := false

// FIXME: remove this eventually
wartremoverErrors in (Test, compile) := Seq()
wartremoverErrors in (Compile, compile) := Seq()
