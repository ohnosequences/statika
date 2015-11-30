package ohnosequences.statika.instructions

/*
## Installation utilities

This module defines convenient types for presenting installation results and methods to work with
them.
*/


import ohnosequences.statika._, results._
import java.io.File


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
    val f: AnyInstructions.sameAs[F] = stupidScala(first)
    f.run(workingDir) match {
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


case class InstructionsSyntax[I <: AnyInstructions](i: I) {

  def -&-[U <: AnyInstructions](u: U): I -&- U = instructions.-&-(i, u)
  def ->-[U <: AnyInstructions](u: U): I ->- U = instructions.->-(i, u)
  def -|-[U <: AnyInstructions { type Out = I#Out }](u: U): I -|- U = instructions.-|-(i, u)
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
