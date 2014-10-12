## Parallel (pair-wise) union of two HLists consisting of AnyTypeSets

```scala
package ohnosequences.statika

import shapeless._
import ohnosequences.cosas._, AnyTypeSet._

trait zUz[F <: HList, S <: HList] { type Out <: HList
  def apply(f: F, s: S): Out

  type is[O <: HList] = zUz[F, S] { type Out = O } 
}

object zUz extends LowPriorityZipUnion {
  implicit def prependHNil[S <: HList] =
    new zUz[HNil, S] { type Out = S
      def apply(f: HNil, s: S) = s 
    }
}

trait LowPriorityZipUnion {
  implicit def appendHNil[F <: HList] =
    new zUz[F, HNil] { type Out = F
      def apply(f: F, s: HNil) = f 
    }

  implicit def zmHList[
      FH <: AnyTypeSet, FT <: HList
    , SH <: AnyTypeSet, ST <: HList
    ](implicit 
      h: FH  ∪  SH
    , t: FT zUz ST
    ) = new      ((FH :: FT) zUz (SH :: ST)) { type Out = h.Out :: t.Out
      def apply(f: FH :: FT,   s: SH :: ST) = 
        (f.head ∪ s.head) :: (f.tail zUz s.tail)
    }
}

trait ZipUnionHLists {
  // adding a method to `HList` with the same name as the trait
  implicit class zipUHList
           [F <: HList](f: F) {
    def zUz[S <: HList](s: S)
      (implicit zm: F zUz S): zm.Out = zm(f, s)
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

[test/scala/InstallWithDepsSuite_Aux.scala]: ../../test/scala/InstallWithDepsSuite_Aux.scala.md
[test/scala/InstallWithDepsSuite.scala]: ../../test/scala/InstallWithDepsSuite.scala.md
[test/scala/BundleTest.scala]: ../../test/scala/BundleTest.scala.md
[main/scala/ZipUnionHLists.scala]: ZipUnionHLists.scala.md
[main/scala/DepsTower.scala]: DepsTower.scala.md
[main/scala/Bundle.scala]: Bundle.scala.md
[main/scala/Distribution.scala]: Distribution.scala.md
[main/scala/package.scala]: package.scala.md
[main/scala/InstallMethods.scala]: InstallMethods.scala.md