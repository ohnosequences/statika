Nice.scalaProject

name := "statika"

organization := "ohnosequences"

bucketSuffix := "era7.com"

libraryDependencies ++= Seq (
    "com.chuusai" % "shapeless_2.10.2" % "2.0.0-M1"
  , "ohnosequences" %% "type-sets" % "0.3.1"
  , "org.scalatest" %% "scalatest" % "2.0" % "test"
  )
