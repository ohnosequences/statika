package ohnosequences.statika.tests

import ohnosequences.statika._
import ohnosequences.cosas._, typeSets._

class InstallWithDepsSuite extends org.scalatest.FunSuite {

  import FooBundles._

  test("Installing Bundles") {

    assert(Bar.installWithEnv(Env).isSuccessful)
    assert(Foo.installWithEnv(Env).isSuccessful)
    assert(Quux.installWithEnv(Env).isSuccessful)
    assert(Buzzz.installWithEnv(Env).isSuccessful)

    assert(Qux.installWithEnv(Env).hasFailures)
    assert(Buzz.installWithEnv(Env).hasFailures)
    assert(Buuzz.installWithEnv(Env).hasFailures)
    assert(Buuzzz.installWithEnv(Env).hasFailures)

  }

}
