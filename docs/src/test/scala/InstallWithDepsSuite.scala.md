
```scala
package ohnosequences.statika.tests

import ohnosequences.statika.instructions._

class InstallWithDepsSuite extends org.scalatest.FunSuite {

  import FooBundles._

  test("Installing Bundles") {

    assert(BarEnv.install(failFast).isSuccessful)
    assert(FooEnv.install(failFast).isSuccessful)
    assert(QuuxEnv.install(failFast).isSuccessful)
    assert(BuzzzEnv.install(failFast).isSuccessful)

    assert(QuxEnv.install(failFast).hasFailures)
    assert(BuzzEnv.install(failFast).hasFailures)
    assert(BuuzzEnv.install(failFast).hasFailures)
    assert(BuuzzzEnv.install(failFast).hasFailures)

  }

}

```




[main/scala/ohnosequences/statika/Bundles.scala]: ../../main/scala/ohnosequences/statika/Bundles.scala.md
[main/scala/ohnosequences/statika/Instructions.scala]: ../../main/scala/ohnosequences/statika/Instructions.scala.md
[test/scala/BundleTest.scala]: BundleTest.scala.md
[test/scala/InstallWithDepsSuite.scala]: InstallWithDepsSuite.scala.md
[test/scala/InstallWithDepsSuite_Aux.scala]: InstallWithDepsSuite_Aux.scala.md