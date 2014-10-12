
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


```scala
package ohnosequences.statika

import shapeless._
import ohnosequences.cosas._, AnyTypeSet._

trait TowerFor[Ds <: AnyTypeSet] { type Out <: HList
  def apply(l: Ds): Out
}

object TowerFor {
  implicit def dtHNil = new TowerFor[∅] { type Out = HNil
    def apply(l: ∅) = HNil: HNil
  }

  implicit def dtHList[
    H <: AnyBundle, T <: AnyTypeSet
  , HOut <: HList, TOut <: HList
  ](implicit 
    tower:  towerFor[T]#is[TOut]
  , concat: (H#DepsTower :+ (H :~: ∅))#is[HOut] 
  , zipU: HOut zUz TOut
  ) = new TowerFor[H :~: T] { type Out = zipU.Out
      def apply(l: H :~: T) = 
        zipU( concat(l.head.depsTower, (l.head :~: ∅) :: HNil), tower(l.tail) )
    }
}
```

Adding `.tower` method to any `TypeSet` consisting of bundles:

```scala
trait DepsTower {
  implicit class TowerHList[D <: AnyTypeSet : ofBundles](l: D) {
    def tower[T <: HList](implicit t: towerFor[D]#is[T]): T = t(l)
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