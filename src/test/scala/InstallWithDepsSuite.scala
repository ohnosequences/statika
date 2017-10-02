package ohnosequences.statika.tests

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
