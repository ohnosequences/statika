### Index

+ src
  + .DS_Store
  + main
    + .DS_Store
    + scala
      + [Bundle.scala](Bundle.md)
      + [DepsTower.scala](DepsTower.md)
      + [Distribution.scala](Distribution.md)
      + [InstallMethods.scala](InstallMethods.md)
      + [package.scala](package.md)
      + [ZipUnionHLists.scala](ZipUnionHLists.md)
  + test
    + .DS_Store
    + scala
      + [BundleTest.scala](../../test/scala/BundleTest.md)
      + [InstallWithDepsSuite.scala](../../test/scala/InstallWithDepsSuite.md)
      + [InstallWithDepsSuite_Aux.scala](../../test/scala/InstallWithDepsSuite_Aux.md)

------

## Parallel (pair-wise) union of two HLists consisting of TypeSets

```scala
package ohnosequences.statika

import shapeless._
import ohnosequences.typesets._

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
      FH <: TypeSet, FT <: HList
    , SH <: TypeSet, ST <: HList
    ](implicit 
      h: FH  U  SH
    , t: FT zUz ST
    ) = new      ((FH :: FT) zUz (SH :: ST)) { type Out = h.Out :: t.Out
      def apply(f: FH :: FT,   s: SH :: ST) = 
        (f.head U s.head) :: (f.tail zUz s.tail)
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

