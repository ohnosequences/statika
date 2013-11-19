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
import ohnosequences.typesets._

trait TowerFor[Ds <: TypeSet] { type Out <: HList
  def apply(l: Ds): Out
}

object TowerFor {
  implicit def dtHNil = new TowerFor[∅] { type Out = HNil
    def apply(l: ∅) = HNil: HNil
  }

  implicit def dtHList[
    H <: AnyBundle, T <: TypeSet
  , HOut <: HList, TOut <: HList
  ](implicit 
    tower:  towerFor[T]#is[TOut]
  , concat: (H#DepsTower :+ (H :~: ∅))#is[HOut] 
  , zipU: HOut zUz TOut
  ) = new TowerFor[H :~: T] { type Out = zipU.Out
      def apply(l: H :~: T) = 
        zipU( concat(l.head.depsTower, set(l.head) :: HNil), tower(l.tail) )
    }
}

/* Adding `.tower` method to any `TypeSet` consisting of bundles: */
trait DepsTower {
  implicit class TowerHList[D <: TypeSet : ofBundles](l: D) {
    def tower[T <: HList](implicit t: towerFor[D]#is[T]): T = t(l)
  }
}
