/*  Besides the list of top-level dependencies (`deps`), a bundle should know all indirect
  dependencies, because it will need them for the proper installation. So the `depsTower` value
  stores _all_ dependencies of the bundle, but in a format of a **tower**.

  It's an `HList` of `HList`s: first goes the list of bundles which don't depend  on anything
  _0-level_, then bundles, which are dependent on them — _1-level_,  then bundles that can
  depend on 0-level or 1-level and so on.

  So one can evaluate the level of bundle as length of `depsTower` plus one.
*/

package ohnosequences.statika

import shapeless._
import ohnosequences.cosas._, AnyTypeSet._
import ohnosequences.cosas.ops.typeSet._

// trait UnionList[Bs <: HList] extends Fn1[Bs] with OutBound[AnyTypeSet]

// object UnionList {

//   implicit def empty:
//       UnionList[HNil] with Out[∅] =
//   new UnionList[HNil] with Out[∅] {
//     def apply(i: In1): Out = ∅
//   }

//   implicit def cons[
//     H <: AnyTypeSet, T <: HList,
//     Rest <: AnyTypeSet, Union <: AnyTypeSet
//   ](implicit
//     rec: UnionList[T] { type Out = Rest },
//     union: (H ∪ Rest) { type Out = Union }
//   ):  UnionList[H :: T] with Out[Union] =
//   new UnionList[H :: T] with Out[Union] {
//     def apply(i: In1): Out = {
//       union(i.head, rec(i.tail))
//     }
//   }
// }

trait NextLevel[Bs <: AnyTypeSet] extends Fn1[Bs] with OutBound[AnyTypeSet]

// case object deps extends Poly1 {
//   implicit def default[B <: AnyBundle] = at[B] {
//     b => b.deps
//   }
// }

object NextLevel {

  type Aux[T <: AnyTypeSet, N <: AnyTypeSet] = NextLevel[T] { type Out = N }
  type Uni[A <: AnyTypeSet, B <: AnyTypeSet, O <: AnyTypeSet] = (A ∪ B) { type Out = O }

  implicit def empty:
      NextLevel[∅] with Out[∅] =
  new NextLevel[∅] with Out[∅] {
    def apply(i: In1): Out = ∅
  }

  implicit def cons[
    H <: AnyBundle, T <: AnyTypeSet,
    TNext <: AnyTypeSet, O <: AnyTypeSet
  ](implicit
    // next: MapToHList[deps.type, Curr] { type Out = Next },
    // union: UnionList[Next] { type Out = O }
    // next: NextLevel[T] { type Out = TNext },
    // union: (H#Deps ∪ TNext) { type Out = O }
    next: Lazy[Aux[T, TNext]],
    union: Lazy[Uni[H#Deps, TNext, O]]
  ):  NextLevel[H :~: T] with Out[O] =
  new NextLevel[H :~: T] with Out[O] {
    def apply(curr: In1): Out = {
      union.value(curr.head.deps, next.value(curr.tail))
    }
  }

}

trait Levels[Curr <: AnyTypeSet] extends Fn1[Curr] with OutBound[HList]

object Levels extends Levels_2 {

  implicit def empty:
      Levels[∅] with Out[HNil] =
  new Levels[∅] with Out[HNil] {
    def apply(i: In1): Out = HNil
  }
}

trait Levels_2 {

  implicit def makeStep[
    Curr <: AnyTypeSet, Next <: AnyTypeSet, Rest <: HList
  ](implicit
    next: NextLevel[Curr] { type Out = Next },
    rec: Levels[Next] { type Out = Rest }
  ):  Levels[Curr] with Out[Curr :: Rest] =
  new Levels[Curr] with Out[Curr :: Rest] {
    def apply(curr: In1): Out = curr :: rec(next(curr))
  }
}

// trait Accumulate[Curr <: AnyTypeSet, Next <: AnyTypeSet] extends Fn2[Curr, Next] with OutBound[AnyTypeSet]

// object Accumulate {

//   implicit def makeStep[
//     Curr <: AnyTypeSet, Next <: AnyTypeSet,
//     Filtered <: AnyTypeSet, Acc <: AnyTypeSet
//   ](implicit
//     sub: (Curr \ Next) { type Out = Filtered },
//     acc: (Next Prepend Filtered) { type Out = Acc}
//   ):  Accumulate[Curr, Next] with Out[Acc] =
//   new Accumulate[Curr, Next] with Out[Acc] {
//     def apply(curr: In1, next: In2): Out = acc(next, sub(curr, next))
//   }
// }

// trait Flatten[Curr <: AnyTypeSet, Acc <: AnyTypeSet] extends Fn2[Curr, Acc] with OutBound[AnyTypeSet]

// object Flatten {

//   implicit def empty[Acc <: AnyTypeSet]:
//       Flatten[∅, Acc] with Out[Acc] =
//   new Flatten[∅, Acc] with Out[Acc] {
//     def apply(curr: In1, acc: In2): Out = acc
//   }

//   implicit def cons[
//     H <: AnyBundle, T <: AnyTypeSet, CurrAcc <: AnyTypeSet,
//     Filtered <: AnyTypeSet,
//     Next <: AnyTypeSet, NextAcc <: AnyTypeSet,
//     Res <: AnyTypeSet
//   ](implicit
//     sub: (CurrAcc \ (H :~: T)) { type Out = Filtered },
//     prep: ((H :~: T) Prepend Filtered) { type Out = NextAcc},
//     next: NextLevel[H :~: T] { type Out = Next },
//     rec: Flatten[Next, NextAcc] { type Out = Res }
//   ):  Flatten[H :~: T, CurrAcc] with Out[Res] =
//   new Flatten[H :~: T, CurrAcc] with Out[Res] {
//     def apply(curr: In1, acc: In2): Out = {
//       println(acc)
//       rec(next(curr), prep(curr, sub(acc, curr)))
//     }
//   }

// }

// trait Flfl[Curr <: AnyTypeSet, Acc <: AnyTypeSet] extends Fn2[Curr, Acc] with OutBound[AnyTypeSet]

// object Flfl {

//   implicit def empty[Acc <: AnyTypeSet]:
//       Flfl[∅, Acc] with Out[Acc] =
//   new Flfl[∅, Acc] with Out[Acc] {
//     def apply(curr: In1, acc: In2): Out = acc
//   }

//   implicit def cons[
//     H <: AnyBundle, T <: AnyTypeSet, CurrAcc <: AnyTypeSet,
//     Filtered <: AnyTypeSet,
//     Next <: AnyTypeSet, NextAcc <: AnyTypeSet,
//     Res <: AnyTypeSet
//   ](implicit
//     sub: (CurrAcc \ (H :~: T)) { type Out = Filtered },
//     prep: ((H :~: T) Prepend Filtered) { type Out = NextAcc},
//     next: NextLevel[H :~: T] { type Out = Next },
//     rec: Flatten[Next, NextAcc] { type Out = Res }
//   ):  Flfl[H :~: T, CurrAcc] with Out[Res] =
//   new Flfl[H :~: T, CurrAcc] with Out[Res] {
//     def apply(curr: In1, acc: In2): Out = rec(next(curr), prep(curr, sub(acc, curr)))
//   }

// }
