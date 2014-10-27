Nice.scalaProject

name := "statika"

organization := "ohnosequences"

bucketSuffix := "era7.com"

scalaVersion := "2.11.0"

libraryDependencies ++= Seq (
  "com.chuusai" %% "shapeless" % "2.0.0",
  "ohnosequences" %% "type-sets" % "0.4.0-SNAPSHOT",
  "org.scalatest" % "scalatest_2.11" % "2.2.0" % "test"
)
