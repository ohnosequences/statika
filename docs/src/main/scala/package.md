### Index

+ src
  + main
    + scala
      + [Bundle.scala](Bundle.md)
      + [DepsTower.scala](DepsTower.md)
      + [Distribution.scala](Distribution.md)
      + [InstallMethods.scala](InstallMethods.md)
      + [package.scala](package.md)
      + [ZipUnionHLists.scala](ZipUnionHLists.md)
  + test
    + scala
      + [BundleTest.scala](../../test/scala/BundleTest.md)
      + [InstallWithDepsSuite.scala](../../test/scala/InstallWithDepsSuite.md)
      + [InstallWithDepsSuite_Aux.scala](../../test/scala/InstallWithDepsSuite_Aux.md)

------

## Statika package object

This is a package object which mixes other parts of statika, to provide a common namespace, where
all the definitions are accessible.


```scala
package ohnosequences

import shapeless._
import shapeless.ops.hlist.Prepend
import ohnosequences.typesets._

package object statika 
 extends DepsTower
  with ZipUnionHLists
  with InstallMethods {
```

### Type aliases for Bundles

```scala
  type dependsOn[B <: AnyBundle] = { type is[X <: AnyBundle] = B ∈ X#Deps }

  // evidence that the given `TypeSet` consists only of bundles
  type ofBundles[S <: TypeSet] = boundedBy[AnyBundle]#is[S]
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
  type towerFor[Ds <: TypeSet] = { type is[O <: HList] = TowerFor[Ds] { type Out = O } }
```

### Implicit conversion from ProcessBuilder-like things to InstallResults

```scala
  implicit def cmdToResult[T : CmdLike](cmd: T): InstallResults = runCommand(cmd)()
  implicit def resultsToList[T : IsResult](r: T): List[InstallResult] = r.trace
  
}

```

