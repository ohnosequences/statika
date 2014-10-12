
## Statika package object

This is a package object which mixes other parts of statika, to provide a common namespace, where
all the definitions are accessible.


```scala
package ohnosequences

import shapeless._
import shapeless.ops.hlist.Prepend
import ohnosequences.cosas._, AnyTypeSet._

package object statika 
 extends DepsTower
  with ZipUnionHLists
  with InstallMethods {
```

### Type aliases for Bundles

```scala
  type dependsOn[B <: AnyBundle] = { type is[X <: AnyBundle] = B ∈ X#Deps }

  // evidence that the given `TypeSet` consists only of bundles
  type ofBundles[S <: AnyTypeSet] = boundedBy[AnyBundle]#is[S]
```

 Some context bounds conveniences (`Prepend` not `Merge`!)
 for saying: `[ LM <: HList : (L ::: M)#is ]`
 or `implicit append: (L :+ X)#is[LX]`


```scala
  type :::[L <: HList, M <: HList] = { type is[O <: HList] = Prepend.Aux[L, M, O] }
  type :+[L <: HList, X] = { type is[O <: HList] = (L ::: (X :: HNil))#is[O] }
```

### Deps Tower

```scala
  type towerFor[Ds <: AnyTypeSet] = { type is[O <: HList] = TowerFor[Ds] { type Out = O } }
```

### Implicit conversion from ProcessBuilder-like things to InstallResults

```scala
  implicit def cmdToResult[T : CmdLike](cmd: T): InstallResults = runCommand(cmd)()
  implicit def resultsToList[T : IsResult](r: T): List[InstallResult] = r.trace
  
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