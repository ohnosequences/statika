Nice.scalaProject

name := "statika"

organization := "ohnosequences"

bucketSuffix := "era7.com"

scalaVersion := "2.11.0-M7"

libraryDependencies ++= Seq (
  "com.chuusai" % "shapeless" % "2.0.0-M1" cross CrossVersion.full,
  "ohnosequences" % "type-sets" % "0.4.0-SNAPSHOT" cross CrossVersion.full,
  "org.scalatest" % "scalatest_2.10" % "2.0" % "test"
)
