
```scala
package ohnosequences.statika.tests

import ohnosequences.statika._, bundles._, installMethods._
import ohnosequences.cosas._, typeSets._
import ohnosequences.cosas.ops.typeSets._
import sys.process._

object FooBundles {

  abstract class TestBundle[Ds <: AnyTypeSet.Of[AnyBundle]]
    (deps:  Ds = ∅)(implicit getDepsList: ToList[Ds] { type Out = List[AnyBundle] })
      extends Bundle(deps)(getDepsList) {

    def install: InstallResults = success(name + " is installed")
  }


  case object Bar extends TestBundle(∅)

  case object Foo extends Bundle(Bar :~: ∅) {
    override def install: InstallResults = 
      "ls" #| "grep .sbt" -&- 
      "echo Foo" ->- 
      success(fullName)
  }


  case object Quux extends TestBundle(Bar :~: Foo :~: ∅) 
  case object Qux  extends Bundle(Foo :~: Bar :~: ∅) {

    def dir(d: String) = new java.io.File(d)

    override def install: InstallResults = 
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
    def install: InstallResults = success(s"Environment ${name} is set up")
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


```


------

### Index

+ src
  + main
    + scala
      + [Bundles.scala][main/scala/Bundles.scala]
      + [InstallMethods.scala][main/scala/InstallMethods.scala]
  + test
    + scala
      + [BundleTest.scala][test/scala/BundleTest.scala]
      + [InstallWithDepsSuite.scala][test/scala/InstallWithDepsSuite.scala]
      + [InstallWithDepsSuite_Aux.scala][test/scala/InstallWithDepsSuite_Aux.scala]

[main/scala/Bundles.scala]: ../../main/scala/Bundles.scala.md
[main/scala/InstallMethods.scala]: ../../main/scala/InstallMethods.scala.md
[test/scala/BundleTest.scala]: BundleTest.scala.md
[test/scala/InstallWithDepsSuite.scala]: InstallWithDepsSuite.scala.md
[test/scala/InstallWithDepsSuite_Aux.scala]: InstallWithDepsSuite_Aux.scala.md