package ohnosequences.statika.tests

import ohnosequences.statika._
import sys.process._

object FooBundles {

  abstract class TestBundle(d: AnyBundle*) extends Bundle(d: _*) {

    def instructions: AnyInstructions = say(bundleName + " is installed")
  }


  case object Bar extends TestBundle

  case object Foo extends Bundle(Bar) {
    def instructions: AnyInstructions =
      cmd("ls")() -&-
      cmd("echo")("Foo") ->-
      say(bundleFullName)
  }


  case object Quux extends TestBundle(Bar, Foo)
  case object Qux  extends Bundle(Foo, Bar) {

    def dir(d: String) = new java.io.File(d)

    def instructions: AnyInstructions =
      Seq("echo", "bar") -&-
      cmd("cat")("qux") ->-
      failure("just wanna fail") -&-
      cmd("ls")("-al") -&-
      say(bundleName)
  }

  case object Buzz  extends TestBundle(Foo, Qux)
  case object Buzzz extends TestBundle(Quux, Foo)

  case object Buuzz  extends TestBundle(Bar, Qux)
  case object Buuzzz extends TestBundle(Qux, Bar)


  case object Env extends Environment {
    def instructions: AnyInstructions = say(s"Environment ${bundleName} is set up")
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
