package ohnosequences.statika.tests

import ohnosequences.statika._

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
