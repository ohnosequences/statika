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
