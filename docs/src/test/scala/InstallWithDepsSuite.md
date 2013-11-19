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
      + [InstallWithDepsSuite.scala](InstallWithDepsSuite.md)
      + [InstallWithDepsSuite_Aux.scala](InstallWithDepsSuite_Aux.md)

------


```scala
package ohnosequences.statika.tests

import ohnosequences.statika._
import ohnosequences.typesets._

class InstallWithDepsSuite extends org.scalatest.FunSuite {

  import FooBundles._

  test("Installing Bundles") {

    // checking that the corresponding hierarchies produce the same towers
    implicitly[Qux.DepsTower ~~ Quux.DepsTower]
    implicitly[Buzz.DepsTower ~~ Buzzz.DepsTower]
    implicitly[Buuzz.DepsTower ~~ Buuzzz.DepsTower]

    assert(Dist.installWithDeps(Bar).isSuccessful)
    assert(Dist.installWithDeps(Foo).isSuccessful)
    assert(Dist.installWithDeps(Quux).isSuccessful)
    assert(Dist.installWithDeps(Buzzz).isSuccessful)

    assert(Dist.installWithDeps(Qux).hasFailures)
    assert(Dist.installWithDeps(Buzz).hasFailures)
    assert(Dist.installWithDeps(Buuzz).hasFailures)
    assert(Dist.installWithDeps(Buuzzz).hasFailures)

  }

}

```

