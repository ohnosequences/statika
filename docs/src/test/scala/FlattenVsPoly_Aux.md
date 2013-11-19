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
 
object xy {
 
  import shapeless._
 
  val a = 1

  val l  = a :: a :: a :: a :: a :: a :: a :: a :: a :: a :: a :: a :: a :: a :: a :: a :: 
    HNil
 
  val ll = l :: l :: l :: l :: l :: l :: l :: l :: l :: l :: l :: l :: l :: l :: l :: l :: 
    HNil

  // val lll = ll :: ll :: ll :: ll :: HNil
 
 
  object x extends (Int -> Int)( i => i )
 
  object y extends Poly1 {
      implicit val int = at[Int]{ i => i }
 
      type OkList[L <: HList] = MapFolder[L, Int, y.type]
 
      implicit def hlist[L <: HList : OkList] = 
        at[L]{ _.foldMap(0)(y)(_ + _) }
  }
 
}

```

