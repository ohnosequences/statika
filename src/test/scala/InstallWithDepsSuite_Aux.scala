package ohnosequences.statika.tests

import ohnosequences.statika._, bundles._, instructions._
import ohnosequences.cosas._, typeSets._
import ohnosequences.cosas.ops.typeSets._
import sys.process._

object FooBundles {

  abstract class TestBundle[Ds <: AnyTypeSet.Of[AnyBundle]]
    (deps:  Ds = ∅)(implicit getDepsList: ToList[Ds] { type Out = List[AnyBundle] })
      extends Bundle(deps)(getDepsList) {

    def install: Results = success(name + " is installed")
  }


  case object Bar extends TestBundle(∅)

  case object Foo extends Bundle(Bar :~: ∅) {
    def install: Results =
      "ls" #| "grep .sbt" -&-
      "echo Foo" ->-
      success(fullName)
  }


  case object Quux extends TestBundle(Bar :~: Foo :~: ∅)
  case object Qux  extends Bundle(Foo :~: Bar :~: ∅) {

    def dir(d: String) = new java.io.File(d)

    def install: Results =
      Seq("echo", "bar") -&-
      "cat qux" @@ dir(".") -&- // should fail here
      "ls -al" @@ dir("/.") ->-
      success(name)
  }

  case object Buzz  extends TestBundle(Foo :~: Qux :~: ∅)
  case object Buzzz extends TestBundle(Quux :~: Foo :~: ∅)

  case object Buuzz  extends TestBundle(Bar :~: Qux :~: ∅)
  case object Buuzzz extends TestBundle(Qux :~: Bar :~: ∅)


  case object Env extends Environment(∅) {
    def install: Results = success(s"Environment ${name} is set up")
  }

  case object TestMetadata extends AnyArtifactMetadata {

    val organization: String  = "ohnosequences"
    val artifact: String      = "statika"
    val version: String       = "2.0.0"
    val artifactUrl: String   = "whatever"
  }
  
  implicit object BarEnv    extends Compatible(Env, Bar, TestMetadata)
  implicit object FooEnv    extends Compatible(Env, Foo, TestMetadata)
  implicit object QuuxEnv   extends Compatible(Env, Quux, TestMetadata)
  implicit object QuxEnv    extends Compatible(Env, Qux, TestMetadata)
  implicit object BuzzEnv   extends Compatible(Env, Buzz, TestMetadata)
  implicit object BuzzzEnv  extends Compatible(Env, Buzzz, TestMetadata)
  implicit object BuuzzEnv  extends Compatible(Env, Buuzz, TestMetadata)
  implicit object BuuzzzEnv extends Compatible(Env, Buuzzz, TestMetadata)
}
