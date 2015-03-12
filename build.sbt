Nice.scalaProject

name := "statika"
organization := "ohnosequences"
description := "Managing dependencies in compile time"

scalaVersion := "2.11.6"
bucketSuffix := "era7.com"

libraryDependencies ++= Seq (
  "com.chuusai" %% "shapeless" % "2.0.0",
  "ohnosequences" %% "cosas" % "0.6.0-SNAPSHOT",
  "org.scalatest" %% "scalatest" % "2.2.2" % "test"
)
