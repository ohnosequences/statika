Nice.scalaProject

name := "statika"
organization := "ohnosequences"
description := "Managing dependencies in compile time"

scalaVersion := "2.11.6"
bucketSuffix := "era7.com"

libraryDependencies ++= Seq (
  "ohnosequences" %% "cosas" % "0.6.0",
  "org.scalatest" %% "scalatest" % "2.2.4" % Test
)
