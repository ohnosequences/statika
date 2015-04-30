
```scala
package ohnosequences.statika.tests

import ohnosequences.statika.instructions._

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
  + test
    + scala
      + [InstallWithDepsSuite_Aux.scala][test/scala/InstallWithDepsSuite_Aux.scala]
      + [InstallWithDepsSuite.scala][test/scala/InstallWithDepsSuite.scala]
      + [BundleTest.scala][test/scala/BundleTest.scala]
  + main
    + scala
      + ohnosequences
        + statika
          + [Bundles.scala][main/scala/ohnosequences/statika/Bundles.scala]
          + [Instructions.scala][main/scala/ohnosequences/statika/Instructions.scala]

[test/scala/InstallWithDepsSuite_Aux.scala]: InstallWithDepsSuite_Aux.scala.md
[test/scala/InstallWithDepsSuite.scala]: InstallWithDepsSuite.scala.md
[test/scala/BundleTest.scala]: BundleTest.scala.md
[main/scala/ohnosequences/statika/Bundles.scala]: ../../main/scala/ohnosequences/statika/Bundles.scala.md
[main/scala/ohnosequences/statika/Instructions.scala]: ../../main/scala/ohnosequences/statika/Instructions.scala.md