package ohnosequences.statika

/*
## Installation utilities

This module defines convenient types for presenting installation results and methods to work with
them.
*/

case object results {

  sealed trait AnyResult { self =>

    type O
    val  out: Option[O]

    val trace: Seq[String]
    val hasFailures: Boolean
    lazy val isSuccessful: Boolean = ! hasFailures

    @SuppressWarnings(Array("org.brianmckenna.wartremover.warts.AsInstanceOf", "org.brianmckenna.wartremover.warts.IsInstanceOf"))
    final def prependTrace(msgs: Seq[String]): AnyResult { type O = self.O } = this match {

      case Failure(tr) => Failure(msgs ++ tr)

      case Success(tr, v:O) => Success(msgs ++ tr, v)
    }
  }

  sealed abstract class Result[O0] extends AnyResult {
    type O = O0
  }

  case class Failure[O0](val trace: Seq[String]) extends Result[O0] {

    val out: Option[O] = None
    val hasFailures = true

    // def prependTrace(msgs: Seq[String]): Failure[O0] = Failure(msgs ++ trace)
  }

  object Failure {

    def apply[O](msg: String): Failure[O] = Failure(Seq(msg))
  }

  case class Success[O0](val trace: Seq[String], o: O0) extends Result[O0] {
    val  out: Option[O0] = Some(o)
    val hasFailures = false

    // def prependTrace(msgs: Seq[String]): Success[O0] = Success[O0](msgs ++ trace, o)
  }

  object Success {

    def apply[O](msg: String, o: O): Success[O] = Success[O](Seq(msg), o)
  }


  // implicit def resultSyntax[R <: AnyResult](r: Result[O]): ResultSyntax[O] = ResultSyntax[O](r)
  // case class ResultSyntax[O](r: Result[O]) {
  //   def +:(msgs: Seq[String]): Result[O] = r.prependTrace(msgs)
  // }

}


case object instructions {

  import java.io.File
  import results._


  trait AnyInstructions {

    type Out

    type R = AnyResult { type O = Out }
    def run(workingDir: File): R
  }

  object AnyInstructions {

    type sameAs[I <: AnyInstructions] = I with AnyInstructions { type Out = I#Out }

    type withOut[O] = AnyInstructions { type Out = O }
  }

  trait Instructions[O] extends AnyInstructions {

    type Out = O
    // type R = AnyResult { type O = Out }
  }

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

    // type R = Second#R

    final def run(workingDir: File): R = {
      first.run(workingDir) match {
        case Failure(tr)  => Failure[Second#Out](tr)
        case Success(tr1, x) => {

          val s: AnyInstructions.sameAs[S] = stupidScala(second)
          val uh: AnyResult { type O = S#Out } = s.run(workingDir)
          uh.prependTrace(tr1)
        }
      }
    }

    override def toString = s"(${first.toString} -&- ${second.toString})"
  }


  /* This executes both steps irrespectively of the result of the first one */
  case class ->-[
    F <: AnyInstructions,
    S <: AnyInstructions
  ](val first: F,//AnyInstructions.sameAs[F],
    val second: S //AnyInstructions.sameAs[S]
  ) extends AnyCombinedInstructions with Instructions[S#Out] {
    type First = F
    type Second = S

    final def run(workingDir: File): R = {

      val s: AnyInstructions.sameAs[S] = stupidScala(second)
      s.run(workingDir).prependTrace( first.run(workingDir).trace )
    }

    override def toString = s"(${first.toString} ->- ${second.toString})"
  }


  /* This executes second step only if the first one failed */
  case class -|-[
    F <: AnyInstructions,
    S <: AnyInstructions { type Out = F#Out }
  ](val first: F,//AnyInstructions.sameAs[F],
    val second: S//AnyInstructions.sameAs[S]
  ) extends AnyCombinedInstructions with Instructions[S#Out] {
    type First = F
    type Second = S

    final def run(workingDir: File): R = {

      val f: AnyInstructions.sameAs[F] = stupidScala(first)
      f.run(workingDir) match {
        case Failure(tr) => second.run(workingDir).prependTrace(tr)
        case s@Success(tr, x) => s
      }
    }

    override def toString = s"(${first.toString} -|- ${second.toString})"
  }


  implicit def instructionsSyntax[X, I <: AnyInstructions](x: I):
    InstructionsSyntax[I] =
    InstructionsSyntax[I](x)

  case class InstructionsSyntax[I <: AnyInstructions](i: I) {

    def -&-[U <: AnyInstructions](u: U): I -&- U = instructions.-&-(i, u)
    def ->-[U <: AnyInstructions](u: U): I ->- U = instructions.->-(i, u)
    def -|-[U <: AnyInstructions { type Out = I#Out }](u: U): I -|- U = instructions.-|-(i, u)
  }

  @SuppressWarnings(Array("org.brianmckenna.wartremover.warts.AsInstanceOf", "org.brianmckenna.wartremover.warts.IsInstanceOf"))
  def stupidScala[I <: AnyInstructions](i: I): AnyInstructions.sameAs[I] = {

    println { s"this is really ${i}"}
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
      Try( Process(seq, workingDir).!! ) match {
        case util.Success(output) => Success[String](Seq(seq.mkString(" ")), output)
        case util.Failure(e) => Failure[String](Seq(e.getMessage))
      }
  })

  def cmd(command: String)(args: String*): CmdInstructions = CmdInstructions(command +: args)
  implicit def seqToInstructions(s: Seq[String]): CmdInstructions = CmdInstructions(s)

}
