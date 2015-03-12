/* ## Prepend first set to the second (i.e. add new elements to the left) */

package ohnosequences.statika

import ohnosequences.cosas._, fns._, typeSets._

@annotation.implicitNotFound(msg = "Can't prepend ${Q} from ${S}")
trait Prepend[S <: AnyTypeSet, Q <: AnyTypeSet] extends Fn2[S, Q] with OutBound[AnyTypeSet]

/* * Case when S is inside Q => result is Q: */
object Prepend extends Prepend_2 {

  implicit def sInQ[S <: AnyTypeSet.SubsetOf[Q], Q <: AnyTypeSet]:
        (S Prepend Q) with Out[Q] = 
    new (S Prepend Q) with Out[Q] { def apply(s: S, q: Q) = q }
}

trait Prepend_2 extends Prepend_3 {
  /* * Case when Q is empty => result is S: */
  implicit def qEmpty[S <: AnyTypeSet]: 
        (S Prepend ∅) with Out[S] =
    new (S Prepend ∅) with Out[S] { def apply(s: S, q: ∅) = s }

  /* * Case when S.head ∈ Q => result is Prepend[S.tail, Q]: */
  implicit def sConsWithoutHead[H, T <: AnyTypeSet,  Q <: AnyTypeSet, TO <: AnyTypeSet] 
    (implicit 
      h: H ∈ Q, 
      rest: (T Prepend Q) { type Out = TO }
    ):  ((H :~: T) Prepend Q) with Out[TO] =
    new ((H :~: T) Prepend Q) with Out[TO] { def apply(s: H :~: T, q: Q) = rest(s.tail, q) }
}

/* * Case when we add S.head and traverse further: */
trait Prepend_3 {
  implicit def sConsAnyHead[H, T <: AnyTypeSet, Q <: AnyTypeSet, TO <: AnyTypeSet] 
    (implicit 
      h: H ∉ Q, 
      rest: (T Prepend Q) { type Out = TO }
    ):  ((H :~: T) Prepend Q) with Out[H :~: TO] =
    new ((H :~: T) Prepend Q) with Out[H :~: TO] { def apply(s: H :~: T, q: Q) = s.head :~: rest(s.tail, q) }
}
