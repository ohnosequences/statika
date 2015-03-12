/* ## Parallel (pair-wise) union of two HLists consisting of AnyTypeSets */

package ohnosequences.statika

import shapeless._
import ohnosequences.cosas._, fns._, typeSets._

trait zUz[F <: HList, S <: HList] extends Fn2[F, S] with OutBound[HList] {
 // type Out <: HList
  // def apply(f: F, s: S): Out

  // type is[O <: HList] = zUz[F, S] { type Out = O } 
}

object zUz extends LowPriorityZipUnion {

  implicit def prependHNil[S <: HList]:
      zUz[HNil, S] with Out[S] =
  new zUz[HNil, S] with Out[S] { def apply(f: HNil, s: S) = s }
}

trait LowPriorityZipUnion {

  implicit def appendHNil[F <: HList]:
      zUz[F, HNil] with Out[F] =
  new zUz[F, HNil] with Out[F] { def apply(f: F, s: HNil) = f }

  implicit def zmHList[
    FH <: AnyTypeSet, FT <: HList, H,
    SH <: AnyTypeSet, ST <: HList, T <: HList
  ](implicit 
    h: (FH  ∪  SH) { type Out = H },
    t: (FT zUz ST) { type Out = T }
  ):  ((FH :: FT) zUz (SH :: ST)) with Out[H :: T] =
  new ((FH :: FT) zUz (SH :: ST)) with Out[H :: T] {
    def apply(f: FH :: FT,   s: SH :: ST) = 
      h(f.head, s.head) :: t(f.tail, s.tail)
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

