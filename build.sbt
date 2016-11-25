name          := "statika"
organization  := "ohnosequences"
description   := "Managing dependencies"

bucketSuffix := "era7.com"

libraryDependencies ++= Seq (
  "ohnosequences" %% "aws-scala-tools" % "0.17.0-97-g37276e9"
)

testOptions       in Test += Tests.Argument("-oD")
parallelExecution in Test := false

// FIXME: remove this eventually
wartremoverErrors in (Test, compile) := Seq()
wartremoverErrors in (Compile, compile) := Seq()
