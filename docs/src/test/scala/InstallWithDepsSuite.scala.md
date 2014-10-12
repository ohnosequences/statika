
```scala
package ohnosequences.statika.tests

import ohnosequences.statika._
import ohnosequences.cosas._, AnyTypeSet._

class InstallWithDepsSuite extends org.scalatest.FunSuite {

  import FooBundles._

  test("Installing Bundles") {

    // checking that the corresponding hierarchies produce the same towers
    implicitly[Qux.DepsTower ~~ Quux.DepsTower]
    // implicitly[Buzz.DepsTower ~~ Buzzz.DepsTower]
    // implicitly[Buuzz.DepsTower ~~ Buuzzz.DepsTower]

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

[test/scala/InstallWithDepsSuite_Aux.scala]: InstallWithDepsSuite_Aux.scala.md
[test/scala/InstallWithDepsSuite.scala]: InstallWithDepsSuite.scala.md
[test/scala/BundleTest.scala]: BundleTest.scala.md
[main/scala/ZipUnionHLists.scala]: ../../main/scala/ZipUnionHLists.scala.md
[main/scala/DepsTower.scala]: ../../main/scala/DepsTower.scala.md
[main/scala/Bundle.scala]: ../../main/scala/Bundle.scala.md
[main/scala/Distribution.scala]: ../../main/scala/Distribution.scala.md
[main/scala/package.scala]: ../../main/scala/package.scala.md
[main/scala/InstallMethods.scala]: ../../main/scala/InstallMethods.scala.md