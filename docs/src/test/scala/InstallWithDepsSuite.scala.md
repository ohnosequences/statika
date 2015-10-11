
```scala
package ohnosequences.statika.tests

import ohnosequences.statika.instructions._

class InstallWithDepsSuite extends org.scalatest.FunSuite {

  import FooBundles._

  test("Installing Bundles") {

    assert(BarEnv.install.isSuccessful)
    assert(FooEnv.install.isSuccessful)
    assert(QuuxEnv.install.isSuccessful)
    assert(BuzzzEnv.install.isSuccessful)

    assert(QuxEnv.install.hasFailures)
    assert(BuzzEnv.install.hasFailures)
    assert(BuuzzEnv.install.hasFailures)
    assert(BuuzzzEnv.install.hasFailures)

  }

}

```




[main/scala/ohnosequences/statika/Bundles.scala]: ../../main/scala/ohnosequences/statika/Bundles.scala.md
[main/scala/ohnosequences/statika/Instructions.scala]: ../../main/scala/ohnosequences/statika/Instructions.scala.md
[test/scala/BundleTest.scala]: BundleTest.scala.md
[test/scala/InstallWithDepsSuite.scala]: InstallWithDepsSuite.scala.md
[test/scala/InstallWithDepsSuite_Aux.scala]: InstallWithDepsSuite_Aux.scala.md
[test/scala/instructions.scala]: instructions.scala.md