package ohnosequences.statika.tests

import shapeless._
import ohnosequences.statika._
import ohnosequences.cosas._, AnyTypeSet._
import sys.process._

object FooBundles {

  // This is a check that two towers are the same on types. Usefull for tests.
  trait ~~[P <: HList, Q <: HList]
  object ~~ {
    implicit val nils = new (HNil ~~ HNil) {}
    implicit def cons[
      PH <: AnyTypeSet, PT <: HList
    , QH <: AnyTypeSet, QT <: HList
    ](implicit h: PH ~:~ QH, t: PT ~~ QT) = new ((PH :: PT) ~~ (QH :: QT)) {}
  }


  case object Bar extends Bundle()

  case object Foo extends Bundle(Bar :~: ∅) {
    override def install[D <: AnyDistribution](dist: D) = 
      "ls" #| "grep .sbt" -&- 
      "echo Foo" ->- 
      success(fullName)
  }


  case object Quux extends Bundle(Bar :~: Foo :~: ∅) 
  case object Qux  extends Bundle(Foo :~: Bar :~: ∅) {

    def dir(d: String) = new java.io.File(d)

    override def install[D <: AnyDistribution](dist: D) = 
      Seq("echo", "bar") -&-
      "cat qux" @@ dir(".") -&- // should fail here
      "ls -al" @@ dir("/.") ->-
      success(name)
  }

  case object Buzz  extends Bundle(Foo :~: Qux :~: ∅)
  case object Buzzz extends Bundle(Quux :~: Foo :~: ∅)

  case object Buuzz  extends Bundle(Bar :~: Qux :~: ∅)
  case object Buuzzz extends Bundle(Qux :~: Bar :~: ∅)

  val allBundles = {
    Buzz :~: Buzzz :~: Buuzz :~: Buuzzz :~: 
    Bar :~: Foo :~: Qux :~: Quux :~: ∅
  }

  case object Dist extends Distribution(allBundles) {
    def setContext = success("Distribution " + name)
  }

}

