/* ## Parallel (pair-wise) union of two HLists consisting of AnyTypeSets */

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

