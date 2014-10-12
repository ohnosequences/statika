
```scala
package ohnosequences.statika.tests

import shapeless._
import shapeless.poly._
import ohnosequences.statika._
import ohnosequences.cosas._, AnyTypeSet._

import shapeless.ops.hlist._

object genericPrintln extends Poly1 {

  implicit def typeset[L <: HList] = at[L] {

    l: L => println(l)
  }
}

class NameSuite extends org.scalatest.FunSuite { 
  object Foo {
    case object bun extends Bundle()
  }
  println(Foo.bun.fullName)
  println(Foo.bun.name)
}

class BuhSuite extends org.scalatest.FunSuite {

  case class bubun(s: String) extends Bundle()

  case object buh extends Bundle()
  case object yeah extends Bundle()
  case object hey extends Bundle(buh :~: yeah :~: ∅)
  case object limit extends Bundle(hey :~: ∅)

  // was failing here before: (because couldn't flatten deps)
  case object l extends Bundle(limit :~: ∅)
  case object r extends Bundle(limit :~: ∅)
  case object q extends Bundle(l :~: r :~: limit :~: ∅)
  case object w extends Bundle(r :~: hey :~: ∅)
  case object e extends Bundle(q :~: w :~: ∅)

  test("output tower") {

    val dp = e.depsTower
     
    println(dp)
    // println()
  }
}

```


------

### Index

+ src
  + test
    + scala
      + [InstallWithDepsSuite_Aux.scala][test/scala/InstallWithDepsSuite_Aux.scala]
      + [InstallWithDepsSuite.scala][test/scala/InstallWithDepsSuite.scala]
      + [BundleTest.scala][test/scala/BundleTest.scala]
  + main
    + scala
      + [ZipUnionHLists.scala][main/scala/ZipUnionHLists.scala]
      + [DepsTower.scala][main/scala/DepsTower.scala]
      + [Bundle.scala][main/scala/Bundle.scala]
      + [Distribution.scala][main/scala/Distribution.scala]
      + [package.scala][main/scala/package.scala]
      + [InstallMethods.scala][main/scala/InstallMethods.scala]

[test/scala/InstallWithDepsSuite_Aux.scala]: InstallWithDepsSuite_Aux.scala.md
[test/scala/InstallWithDepsSuite.scala]: InstallWithDepsSuite.scala.md
[test/scala/BundleTest.scala]: BundleTest.scala.md
[main/scala/ZipUnionHLists.scala]: ../../main/scala/ZipUnionHLists.scala.md
[main/scala/DepsTower.scala]: ../../main/scala/DepsTower.scala.md
[main/scala/Bundle.scala]: ../../main/scala/Bundle.scala.md
[main/scala/Distribution.scala]: ../../main/scala/Distribution.scala.md
[main/scala/package.scala]: ../../main/scala/package.scala.md
[main/scala/InstallMethods.scala]: ../../main/scala/InstallMethods.scala.md