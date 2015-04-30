package ohnosequences.statika.tests

import ohnosequences.statika.bundles._

class NameSuite extends org.scalatest.FunSuite { 
  object Foo {
    case object bun extends Module
  }
  println(Foo.bun.bundleFullName)
  println(Foo.bun.bundleName)
}

class BuhSuite extends org.scalatest.FunSuite {

  case class bubun(s: String) extends Module

  case object buh extends Module
  case object yeah extends Module
  case object hey extends Module(buh, yeah)
  case object limit extends Module(hey)

  // was failing here before: (because couldn't flatten deps)
  case object l extends Module(limit, hey, buh)
  case object r extends Module(limit)
  case object q extends Module(l, r, limit)
  case object w extends Module(r, hey)
  case object e extends Module(q, w)

  test("output tower") {

    case object x0 extends Module
    case object x1 extends Module(x0)
    case object x2 extends Module(x1, x0)
    case object x3 extends Module(x2)
    case object x4 extends Module(x3)

    println(e.bundleFullDependencies.toString)
    println(e.bundleFullDependencies.toString)
  }
}
