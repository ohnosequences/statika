package ohnosequences.statika.tests

import ohnosequences.statika._
import java.io.File

class InstructionsSuite extends org.scalatest.FunSuite {

  object defs {
    val dir = new File(".")
    val yes = success[Unit]("yes", ())
    val no  = failure[Unit]("no")

    def assertSuccess(i: AnyInstructions): Unit =
      assert{
        print(i.toString + ": ")
        val result = i.run(dir)
        println(result.trace)
        result.isSuccessful == true
      }

    def assertFailure(i: AnyInstructions): Unit =
      assert{
        print(i.toString + ": ")
        val result = i.run(dir)
        println(result.trace)
        result.isSuccessful == false
      }
  }
  import defs._

  test("test -&- combinator") {
    assertFailure{ no  -&- no }
    assertFailure{ yes -&- no }
    assertFailure{ no  -&- yes }
    assertSuccess{ yes -&- yes }
  }

  test("test -|- combinator") {
    assertFailure{ no  -|- no }
    assertSuccess{ yes -|- no }
    assertSuccess{ no  -|- yes }
    assertSuccess{ yes -|- yes }
  }

  test("test ->- combinator") {
    assertFailure{ no  ->- no }
    assertFailure{ yes ->- no }
    assertSuccess{ no  ->- yes }
    assertSuccess{ yes ->- yes }
  }

  test("test two combinators") {
    assertSuccess{ yes -&- yes -|- { yes ->- no } }
    assertFailure{ yes -&- no  -|- { yes ->- no } }
    // val a = Try{println(1/0)}
    assertSuccess{ LazyTry{println(1)} -&- LazyTry{println(2)} -|- { LazyTry{println(3)} } }
  }

  test("test folding instructions") {
    val list = List(say("foo"), say("bar"), say("buh"))
    val fold = list.foldLeft[AnyInstructions](say("nuf"))( _ -&- _ )

    assertSuccess{ fold }
  }
}
