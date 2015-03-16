package ohnosequences.statika.tests

import ohnosequences.statika._, bundles._, environments._, installations._
import ohnosequences.cosas._, typeSets._
import sys.process._

object FooBundles {

  case object Bar extends Module()

  case object Foo extends Module(Bar :~: ∅) {
    override def install[D <: AnyEnvironment](dist: D) = 
      "ls" #| "grep .sbt" -&- 
      "echo Foo" ->- 
      success(fullName)
  }


  case object Quux extends Module(Bar :~: Foo :~: ∅) 
  case object Qux  extends Module(Foo :~: Bar :~: ∅) {

    def dir(d: String) = new java.io.File(d)

    override def install[D <: AnyEnvironment](dist: D) = 
      Seq("echo", "bar") -&-
      "cat qux" @@ dir(".") -&- // should fail here
      "ls -al" @@ dir("/.") ->-
      success(name)
  }

  case object Buzz  extends Module(Foo :~: Qux :~: ∅)
  case object Buzzz extends Module(Quux :~: Foo :~: ∅)

  case object Buuzz  extends Module(Bar :~: Qux :~: ∅)
  case object Buuzzz extends Module(Qux :~: Bar :~: ∅)


  case object Env extends AnyEnvironment {
    def setContext = success(s"Environment ${name} is set up")
  }


  implicit object BarEnv    extends Compatible(Bar, Env)
  implicit object FooEnv    extends Compatible(Foo, Env)
  implicit object QuuxEnv   extends Compatible(Quux, Env)
  implicit object QuxEnv    extends Compatible(Qux, Env)
  implicit object BuzzEnv   extends Compatible(Buzz, Env)
  implicit object BuzzzEnv  extends Compatible(Buzzz, Env)
  implicit object BuuzzEnv  extends Compatible(Buuzz, Env)
  implicit object BuuzzzEnv extends Compatible(Buuzzz, Env)
}

