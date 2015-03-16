
```scala
package ohnosequences.statika.tests

import ohnosequences.statika._, installMethods._
import ohnosequences.cosas._, typeSets._

class InstallWithDepsSuite extends org.scalatest.FunSuite {

  import FooBundles._

  test("Installing Bundles") {

    assert(Bar.installWithEnv(Env, failFast).isSuccessful)
    assert(Foo.installWithEnv(Env, failFast).isSuccessful)
    assert(Quux.installWithEnv(Env, failFast).isSuccessful)
    assert(Buzzz.installWithEnv(Env, failFast).isSuccessful)

    assert(Qux.installWithEnv(Env, failFast).hasFailures)
    assert(Buzz.installWithEnv(Env, failFast).hasFailures)
    assert(Buuzz.installWithEnv(Env, failFast).hasFailures)
    assert(Buuzzz.installWithEnv(Env, failFast).hasFailures)

  }

}

```


------

### Index

+ src
  + main
    + scala
      + [Bundles.scala][main/scala/Bundles.scala]
      + [InstallMethods.scala][main/scala/InstallMethods.scala]
  + test
    + scala
      + [BundleTest.scala][test/scala/BundleTest.scala]
      + [InstallWithDepsSuite.scala][test/scala/InstallWithDepsSuite.scala]
      + [InstallWithDepsSuite_Aux.scala][test/scala/InstallWithDepsSuite_Aux.scala]

[main/scala/Bundles.scala]: ../../main/scala/Bundles.scala.md
[main/scala/InstallMethods.scala]: ../../main/scala/InstallMethods.scala.md
[test/scala/BundleTest.scala]: BundleTest.scala.md
[test/scala/InstallWithDepsSuite.scala]: InstallWithDepsSuite.scala.md
[test/scala/InstallWithDepsSuite_Aux.scala]: InstallWithDepsSuite_Aux.scala.md