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

  trait Instructions[O] extends AnyInstructions { type Out = O }

  trait AnyCombinedInstructions extends AnyInstructions {
    type First <: AnyInstructions
    val  first: First

    type Second <: AnyInstructions
    val  second: Second

    // type Out = Second#Out
  }


  /* This executes second step only if the first one succeeded */
  case class -&-[
    F <: AnyInstructions,
    S <: AnyInstructions
  ](val first: F,
    val second: S
  ) extends AnyCombinedInstructions with Instructions[S#Out] {
    type First = F
    type Second = S

    final def run(workingDir: File): Result[Out] = {
      first.run(workingDir) match {
        case Failure(tr)  => Failure(tr)
        case Success(tr1, x) => {
          val s: AnyInstructions.sameAs[S] = stupidScala(second)
          tr1 +: s.run(workingDir)
        }
      }
    }

    override def toString = s"(${first.toString} -&- ${second.toString})"
  }


  /* This executes both steps irrespectively of the result of the first one */
  case class ->-[
    F <: AnyInstructions,
    S <: AnyInstructions
  ](val first: F,
    val second: S
  ) extends AnyCombinedInstructions with Instructions[S#Out] {
    type First = F
    type Second = S

    final def run(workingDir: File): Result[Out] = {
      val f: AnyInstructions.sameAs[F] = stupidScala(first)
      val s: AnyInstructions.sameAs[S] = stupidScala(second)

      f.run(workingDir).trace +: s.run(workingDir)
    }

    override def toString = s"(${first.toString} ->- ${second.toString})"
  }


  /* This executes second step only if the first one failed */
  case class -|-[
    F <: AnyInstructions,
    S <: AnyInstructions { type Out = F#Out }
  ](val first: F,
    val second: S
  ) extends AnyCombinedInstructions with Instructions[S#Out] {
    type First = F
    type Second = S

    final def run(workingDir: File): Result[Out] = {
      val f: AnyInstructions.sameAs[F] = stupidScala(first)

      f.run(workingDir) match {
        case Failure(tr)  => tr +: second.run(workingDir)
        case s@Success(tr, x) => s
      }
    }

    override def toString = s"(${first.toString} -|- ${second.toString})"
  }


  implicit def instructionsSyntax[X, I <: AnyInstructions](x: X)(implicit toInst: X => I):
    InstructionsSyntax[I] =
    InstructionsSyntax[I](toInst(x))

  case class InstructionsSyntax[I <: AnyInstructions](i: I) {

    def -&-[U <: AnyInstructions](u: U): I -&- U = instructions.-&-(i, u)
    def ->-[U <: AnyInstructions](u: U): I ->- U = instructions.->-(i, u)
    def -|-[U <: AnyInstructions { type Out = I#Out }](u: U): I -|- U = instructions.-|-(i, u)
  }

  @SuppressWarnings(Array("org.brianmckenna.wartremover.warts.AsInstanceOf", "org.brianmckenna.wartremover.warts.IsInstanceOf"))
  final def stupidScala[I <: AnyInstructions](i: I): AnyInstructions.sameAs[I] = {

    i.asInstanceOf[AnyInstructions.sameAs[I]]
  }


  import sys.process.Process
  import util.Try

  class SimpleInstructions[O](r: File => Result[O]) extends Instructions[O] {

    def run(workingDir: File): Result[Out] = r(workingDir)
  }

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



  object LazyTry {
    def apply[T](t: => T): Instructions[T] = new Instructions[T] {

      def run(workingDir: File): Result[Out] = Try(t) match {
        case util.Success(output) => Success[T](this.toString, output)
        case util.Failure(e) => Failure[T](e.getMessage)
      }
    }
  }


  case class CmdInstructions(seq: Seq[String]) extends SimpleInstructions[String]({
    workingDir: File =>
      println("===> " + seq.mkString(" "))
      Try( Process(seq, workingDir).!! ) match {
        case util.Success(output) => Success[String](Seq(seq.mkString(" ")), output)
        case util.Failure(e) => Failure[String](Seq(e.getMessage))
      }
  })

  def cmd(command: String)(args: String*): CmdInstructions = CmdInstructions(command +: args)
  implicit def seqToInstructions(s: Seq[String]): CmdInstructions = CmdInstructions(s)

}
