package ohnosequences.statika.tests

import shapeless._
import shapeless.poly._
import ohnosequences.statika._
import ohnosequences.cosas._, AnyTypeSet._

import shapeless.ops.hlist._

object genericPrintln extends Poly1 {

  implicit def typeset[L <: HList] = at[L] {

    l: L => println(l)
  }
}

class NameSuite extends org.scalatest.FunSuite { 
  object Foo {
    case object bun extends Bundle()
  }
  println(Foo.bun.fullName)
  println(Foo.bun.name)
}

class BuhSuite extends org.scalatest.FunSuite {

  case class bubun(s: String) extends Bundle()

  case object buh extends Bundle()
  case object yeah extends Bundle()
  case object hey extends Bundle(buh :~: yeah :~: ∅)
  case object limit extends Bundle(hey :~: ∅)

  // was failing here before: (because couldn't flatten deps)
  case object l extends Bundle(limit :~: hey :~: buh :~: ∅)
  case object r extends Bundle(limit :~: ∅)
  case object q extends Bundle(l :~: r :~: limit :~: ∅)
  case object w extends Bundle(r :~: hey :~: ∅)
  case object e extends Bundle(q :~: w :~: ∅)

  test("output tower") {

    // case object hub extends Bundle(buh :~: ∅)
    // implicitly[Flatten[hub.type :~: ∅, ∅]](Flatten.cons)
     // { type Out = buh.type :~: hub.type :~: ∅ }]

    case object x0 extends Bundle(∅)
    case object x1 extends Bundle(x0 :~: ∅)
    case object x2 extends Bundle(x1 :~: x0 :~: ∅)
    case object x3 extends Bundle(x2 :~: ∅)
    case object x4 extends Bundle(x3 :~: ∅)
    // case object x5 extends Bundle(x4 :~: ∅)
    // case object x6 extends Bundle(x5 :~: ∅)
    // case object x7 extends Bundle(x6 :~: ∅)
    // case object x8 extends Bundle(x7 :~: ∅)

    import shapeless._, Lazy._

    println("_____________________________")
    println(x2.deps.next(NextLevel.cons).toString)

    // println(hey.name + ": " + hey.deps.levels.toString)
    // println(limit.name + ": " + limit.deps.levels.toString)
    // println(l.name + ": " + l.deps.levels.toString)
    // println(r.name + ": " + r.deps.levels.toString)
    // println(q.name + ": " + q.deps.levels.toString)
    // println(w.name + ": " + w.deps.levels.toString)
    // println(e.name + ": " + e.deps.levels.toString)
    println("_____________________________")
  }
}
