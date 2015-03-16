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
