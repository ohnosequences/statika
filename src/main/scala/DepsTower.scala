/*
## Tower of Bundle's dependencies

Bundle dependencies form a directed acyclic graph (DAG), so it can be [_topologically
ordered_](http://en.wikipedia.org/wiki/Topological_sorting). It means that we can factorize graph
vertices by grade, i.e. level of dependency on other vertices:
- bundles of _0-level_ don't depend on anything;
- bundles of _n-level_ have al least one dependency of level n-1.

So, we want to build for each bundle this _tower_ of full dependencies list levels, using it's
direct dependencies. As every dependency is on lower level, this structure is easy to build by
induction.

Algorithm is very simple: bundles without dependencies have "empty" tower; for a bundle of level n,
we take tower of every it's direct dependency and we know that they are all maximum n-1 height.
Now we just merge (union) all first levels of these towers, all second levels and so on.
The order of bundle on the same level is not important, levels can be merged independently.

See also [Union](shapeless/sets/Union.md) and [ZipUnion](shapeless/ZipUnion.md) modules.
*/

package ohnosequences.statika

import shapeless._
import ohnosequences.cosas._, AnyTypeSet._

trait TowerFor[Ds <: AnyTypeSet] extends Fn1[Ds] with OutBound[HList]

object TowerFor {

  implicit def dtHNil: 
      TowerFor[∅] with Out[HNil] = 
  new TowerFor[∅] with Out[HNil] { def apply(l: In1): Out = HNil }

  implicit def dtHList[
    H <: AnyBundle, T <: AnyTypeSet,
    HTow <: HList, HOut <: HList, 
    TTow <: HList, O <: HList
  ](implicit 
    ttower: TowerFor[T] { type Out = TTow },
    htower: TowerFor[H#Deps] { type Out = HTow },
    concat: (HTow :+ (H :~: ∅))#is[HOut],
    zipU: (HOut zUz TTow) { type Out = O }
  ):  TowerFor[H :~: T] with Out[O] = 
  new TowerFor[H :~: T] with Out[O] {
    def apply(l: In1): Out = 
      zipU( concat(htower(l.head.deps), (l.head :~: ∅) :: HNil), ttower(l.tail) )
  }

}

/* Adding `.tower` method to any `TypeSet` consisting of bundles: */
trait DepsTower {
  implicit class TowerHList[D <: AnyTypeSet.Of[AnyBundle]](l: D) {
    def tower[T <: HList](implicit t: TowerFor[D] { type Out = T }): T = t(l)
  }
}
