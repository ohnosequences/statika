package ohnosequences.statika

/*
## Installation utilities

This module defines convenient types for presenting installation results and methods to work with
them.
*/

case object results {

  sealed trait AnyResult {
    type Out
    val  out: Option[Out]

    val trace: Seq[String]
    val hasFailures: Boolean
    lazy val isSuccessful: Boolean = ! hasFailures

    def prependTrace(msgs: Seq[String]): Result[Out]
  }


  sealed abstract class Result[O] extends AnyResult {
    type Out = O
  }

  case class Failure[O](val trace: Seq[String]) extends Result[O] {
    val out: Option[Out] = None
    val hasFailures = true

    def prependTrace(msgs: Seq[String]): Failure[Out] = Failure[Out](msgs ++ trace)
  }

  object Failure {

    def apply[O](msg: String): Failure[O] = Failure(Seq(msg))
  }

  case class Success[O](val trace: Seq[String], o: O) extends Result[O] {
    val  out: Option[Out] = Some(o)
    val hasFailures = false

    def prependTrace(msgs: Seq[String]): Success[Out] = Success[Out](msgs ++ trace, o)
  }

  object Success {

    def apply[O](msg: String, o: O): Success[O] = Success[O](Seq(msg), o)
  }


  implicit def resultSyntax[O](r: Result[O]): ResultSyntax[O] = ResultSyntax[O](r)
  case class ResultSyntax[O](r: Result[O]) {
    def +:(msgs: Seq[String]): Result[O] = r.prependTrace(msgs)
  }

}


case object instructions {

  import java.io.File
  import results._


  trait AnyInstructions {
    type Out

    def run(workingDir: File): Result[Out]
  }

  object AnyInstructions {

    type sameAs[I <: AnyInstructions] = I with AnyInstructions { type Out = I#Out }

    type withOut[O] = AnyInstructions { type Out = O }
  }

  // trait Instructions[O] extends AnyInstructions { type Out = O }

  trait AnyCombinedInstructions extends AnyInstructions {
    type First <: AnyInstructions
    val  first: AnyInstructions.sameAs[First]

    type Second <: AnyInstructions
    val  second: AnyInstructions.sameAs[Second]

    type Out = Second#Out
  }


  /* Same as combine non-failable, but doesn't append the first trace ("forgets it") */
  case class CombineNonFailable[
    F <: AnyInstructions,
    S <: AnyInstructions
  ](val first: AnyInstructions.sameAs[F],
    val second: AnyInstructions.sameAs[S]
  ) extends AnyCombinedInstructions {
    type First = F
    type Second = S

    final def run(workingDir: File): Result[Out] = {
      first.run(workingDir) match {
        case Failure(tr)  => Failure(tr)
        case Success(tr1, x) => tr1 +: second.run(workingDir)
      }
    }
  }

  type -&-[F <: AnyInstructions, S <: AnyInstructions] = CombineNonFailable[F, S]


  /* Same as combine non-failable, but doesn't append the first trace ("forgets it") */
  case class CombineForgetful[
    F <: AnyInstructions,
    S <: AnyInstructions
  ](val first: AnyInstructions.sameAs[F],
    val second: AnyInstructions.sameAs[S]
  ) extends AnyCombinedInstructions {
    type First = F
    type Second = S

    final def run(workingDir: File): Result[Out] = {
      first.run(workingDir) match {
        case Failure(tr)  => Failure(tr)
        case Success(_, tr1) => second.run(workingDir)
      }
    }
  }

  type ->-[F <: AnyInstructions, S <: AnyInstructions] = CombineForgetful[F, S]


  case class CombineFailable[
    F <: AnyInstructions,
    S <: AnyInstructions
  ](val first: AnyInstructions.sameAs[F],
    val second: AnyInstructions.sameAs[S]
  ) extends AnyCombinedInstructions {
    type First = F
    type Second = S

    final def run(workingDir: File): Result[Out] = {
      first.run(workingDir).trace +: second.run(workingDir)
    }
  }

  type -|-[F <: AnyInstructions, S <: AnyInstructions] = CombineFailable[F, S]


  implicit def instructionsSyntax[X, I <: AnyInstructions](x: X)(implicit toInst: X => AnyInstructions.sameAs[I]):
    InstructionsSyntax[I] =
    InstructionsSyntax[I](toInst(x))

  case class InstructionsSyntax[I <: AnyInstructions](i: AnyInstructions.sameAs[I]) {

    def -&-[U <: AnyInstructions](u: AnyInstructions.sameAs[U]): I -&- U = CombineNonFailable[I, U](i, u)
    def ->-[U <: AnyInstructions](u: AnyInstructions.sameAs[U]): I ->- U = CombineForgetful[I, U](i, u)
    def -|-[U <: AnyInstructions](u: AnyInstructions.sameAs[U]): I -|- U = CombineFailable[I, U](i, u)
  }

  implicit def stupidScala[I <: AnyInstructions](i: I): AnyInstructions.sameAs[I] = i


  import sys.process.Process
  import util.Try

  trait AnySimpleInstructions extends AnyInstructions

  class SimpleInstructions[O](r: File => Result[O]) extends AnySimpleInstructions {
    type Out = O

    def run(workingDir: File): Result[Out] = r(workingDir)
  }

  case class TryHard[O](r: File => Result[O]) extends SimpleInstructions[O](r)

  // case class JustDoIt[O](x: => Result[O]) extends SimpleInstructions[O](_ => x)

  case class say(msg: String) extends SimpleInstructions[Unit](
    _ => Success(Seq(msg), ())
  )

  case class success[O](msg: String, o: O) extends SimpleInstructions[O](
    _ => Success[O](Seq(msg), o)
  )

  case class failure[O](msg: String) extends SimpleInstructions[O](
    _ => Failure[O](Seq(msg))
  )


  case class TryInstructions[X](t: Try[X]) extends SimpleInstructions[X]({
    _ => t match {
      case util.Success(output) => Success[X](Seq(t.toString), output)
      case util.Failure(e) => Failure[X](Seq(e.getMessage))
    }
  })

  implicit def tryToInstructions[T](t: Try[T]): TryInstructions[T] = TryInstructions[T](t)


  case class CmdInstructions(seq: Seq[String]) extends SimpleInstructions[String]({
    workingDir: File =>
      Try( Process(seq, workingDir).!! ) match {
        case util.Success(output) => Success[String](Seq(seq.mkString(" ")), output)
        case util.Failure(e) => Failure[String](Seq(e.getMessage))
      }
  })

  def cmd(command: String)(args: String*): CmdInstructions = CmdInstructions(command +: args)
  implicit def seqToInstructions(s: Seq[String]): CmdInstructions = CmdInstructions(s)

}
