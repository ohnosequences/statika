
```scala
package ohnosequences.statika.tests

import ohnosequences.statika._
import FooBundles._

class InstallWithDepsSuite extends org.scalatest.FunSuite {

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




[main/scala/ohnosequences/statika/aws/amis.scala]: ../../main/scala/ohnosequences/statika/aws/amis.scala.md
[main/scala/ohnosequences/statika/aws/package.scala]: ../../main/scala/ohnosequences/statika/aws/package.scala.md
[main/scala/ohnosequences/statika/bundles.scala]: ../../main/scala/ohnosequences/statika/bundles.scala.md
[main/scala/ohnosequences/statika/compatibles.scala]: ../../main/scala/ohnosequences/statika/compatibles.scala.md
[main/scala/ohnosequences/statika/instructions.scala]: ../../main/scala/ohnosequences/statika/instructions.scala.md
[main/scala/ohnosequences/statika/package.scala]: ../../main/scala/ohnosequences/statika/package.scala.md
[main/scala/ohnosequences/statika/results.scala]: ../../main/scala/ohnosequences/statika/results.scala.md
[test/scala/BundleTest.scala]: BundleTest.scala.md
[test/scala/InstallWithDepsSuite.scala]: InstallWithDepsSuite.scala.md
[test/scala/InstallWithDepsSuite_Aux.scala]: InstallWithDepsSuite_Aux.scala.md
[test/scala/instructions.scala]: instructions.scala.md