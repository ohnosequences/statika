### Index

+ src
  + main
    + scala
      + [Bundle.scala](../../main/scala/Bundle.md)
      + [DepsTower.scala](../../main/scala/DepsTower.md)
      + [Distribution.scala](../../main/scala/Distribution.md)
      + [InstallMethods.scala](../../main/scala/InstallMethods.md)
      + [package.scala](../../main/scala/package.md)
      + [ZipUnionHLists.scala](../../main/scala/ZipUnionHLists.md)
  + test
    + scala
      + [BundleTest.scala](BundleTest.md)
      + [CompilationTimeTest.scala](CompilationTimeTest.md)
      + [FlattenVsPoly.scala](FlattenVsPoly.md)
      + [FlattenVsPoly_Aux.scala](FlattenVsPoly_Aux.md)
      + [InstallWithDepsSuite.scala](InstallWithDepsSuite.md)
      + [InstallWithDepsSuite_Aux.scala](InstallWithDepsSuite_Aux.md)

------


```scala
package ohnosequences.statika.tests
 
import ohnosequences.statika._
 
class FlattenVsPolySuite extends org.scalatest.FunSuite {
 
  /////////////////////////////////////////////////////////////////
  import xy._
 
  test("x vs. y") {
    // 16
    // println(flatten(ll).foldMap(0)(x)(_ + _))
 
    // println(y(ll))
```

 Tests for y. Time is in seconds:
ll\l| 16 | 32 | 48 | 64 |  
––––+––––+––––+––––+––––+  
 16 |  4 |  7 | 11 | 15 |  
 32 | 13 | 19 |    |    |  
 48 | 18 |    |    |    |  
 64 | 32 |    |    |    |  
––––+––––+––––+––––+––––+

```scala
  }
 
}

```

