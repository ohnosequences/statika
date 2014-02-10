Nice.scalaProject

name := "statika"

organization := "ohnosequences"

bucketSuffix := "era7.com"

scalaVersion := "2.11.0-M7"

libraryDependencies ++= Seq (
  "com.chuusai" % "shapeless_2.11.0-M4" % "2.0.0-M1",
  "ohnosequences" % "type-sets_2.11.0-M7" % "0.4.0-SNAPSHOT",
  "org.scalatest" % "scalatest_2.10" % "2.0" % "test"
)

dependencyOverrides ++= Set(
  "org.scala-lang" % "scala-compiler" % "2.11.0-M7"
)
