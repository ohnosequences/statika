package ohnosequences.statika

/*
## Installation utilities

This module defines convenient types for presenting installation results and methods to work with
them.
*/

case object instructions {

  import java.io.File

  sealed trait AnyResult {
    type Out

    val trace: Seq[String]
    val hasFailures: Boolean
    lazy val isSuccessful: Boolean = ! hasFailures

    def prependTrace(msgs: Seq[String]): Result[Out]
  }

  abstract class Result[O] extends AnyResult { type Out = O }

  case class Failure[O](val trace: Seq[String]) extends Result[O] {
    // type Out = O
    val hasFailures = true

    def prependTrace(msgs: Seq[String]): Failure[Out] = Failure[O](msgs ++ trace)
  }
  case class Success[O](val trace: Seq[String], o: O) extends Result[O] {
    // type Out = O
    val  out: Out = o
    val hasFailures = false

    def prependTrace(msgs: Seq[String]): Success[O] = Success[O](msgs ++ trace, o)
  }

  // type Result[O] = AnyResult { type Out <: O }


  implicit def resultSyntax[O](r: Result[O]): ResultSyntax[O] = ResultSyntax[O](r)
  case class ResultSyntax[O](r: Result[O]) {
    def +:(msgs: Seq[String]): Result[O] = r.prependTrace(msgs)
  }


  /* Environment abstracts over the execution context */
  trait AnyEnvironment

  /* But at the moment we are going to use simple file system environment: */
  trait AnyFileSystemEnvironment extends AnyEnvironment {

    val workingDir: File
  }

  case class FileSystemEnvironment(val workingDir: File) extends AnyFileSystemEnvironment

  /* Would be nice to be able to combine different environments and express it on type level,
     but at the moment there is no real need in that and no easy way to do it. */

  /* This is kind of an arrow description of type `In => Result[Out]` */
  trait AnyInstructions {
    type In
    type Out

    /* instructions know about minimal neccessary environment for its execution */
    type Env <: AnyEnvironment

    def run(env: Env, in: In): Result[Out]
  }

  object AnyInstructions {

    type sameAs[I <: AnyInstructions] = I with AnyInstructions {
      type In  = I#In
      type Out = I#Out
      type Env = I#Env
    }
  }

  /* We are going to combine instructions in the following way

     ```
     A => Result[B]
     C => Result[D] where C <: B
     --------------
     A => Result[D]
     ```

     i.e it's a `flatMap`, but with different flavours, depending on how
     we want to process result (failure or success) of the first action.
  */
  trait AnyCombinedInstructions extends AnyInstructions {
    type First <: AnyInstructions
    val  first: AnyInstructions.sameAs[First]

    type Second <: AnyInstructions {
      type In  = First#Out
      // NOTE: this is probably too strict, but fine for now
      type Env = First#Env
    }
    val  second: AnyInstructions.sameAs[Second]

    type Env = First#Env
    type In  = First#In
    type Out = Second#Out
  }

  /* Same as combine non-failable, but doesn't append the first trace ("forgets it") */
  case class -&-[
    F <: AnyInstructions,
    S <: AnyInstructions { type In = F#Out; type Env = F#Env }
  ](val first:  AnyInstructions.sameAs[F],
    val second: AnyInstructions.sameAs[S]
  ) extends AnyCombinedInstructions  {
    type First = F
    type Second = S

    def run(env: Env, in: In): Result[Out] = {
      first.run(env, in) match {
        case Failure(tr) => Failure(tr)
        case Success(tr, out) => tr +: second.run(env, out)
      }
    }
  }

  // type -&-[
  //   F <: AnyInstructions,
  //   S <: AnyInstructions { type In = F#Out; type Env = F#Env }
  // ] = CombineNonFailable[F, S]


  // /* Same as combine non-failable, but doesn't append the first trace ("forgets it") */
  // case class CombineForgetful[
  //   F <: AnyInstructions,
  //   S <: AnyInstructions { type In = F#Out; type Env = F#Env }
  // ](val first:  AnyInstructions.sameAs[F],
  //   val second: AnyInstructions.sameAs[S]
  // ) extends AnyCombinedInstructions {
  //   type First = F
  //   type Second = S
  //
  //   def run(env: Env, in: In): Result[Out] = {
  //     first.run(env, in) match {
  //       case Failure(tr)     => Failure(tr)
  //       case Success(tr1, _) => second.run(workingDir)
  //     }
  //   }
  // }
  //
  // type ->-[F <: AnyInstructions, S <: AnyInstructions] = CombineForgetful[F, S]

  // case class CombineFailable[
  //   // FIXME: sbt warns that this is bad refinement:
  //   F <: AnyInstructions,
  //   S <: AnyInstructions { type In = F#Out with F#In; type Env = F#Env }
  // ](val first:  AnyInstructions.sameAs[F],
  //   val second: AnyInstructions.sameAs[S]
  // ) extends AnyCombinedInstructions {
  //   type First = F
  //   type Second = S
  //
  //   def run(env: Env, in: In): Result[Out] = {
  //     first.run(env, in) match {
  //       case Failure(tr)      => tr +: second.run(env, in)
  //       case Success(tr, out) => tr +: second.run(env, out)
  //     }
  //
  //   }
  // }

  // type -|-[
  //   F <: AnyInstructions { type Out = F#In },
  //   S <: AnyInstructions { type In = F#Out; type Env = F#Env }
  // ] = CombineFailable[F, S]


  import sys.process.Process
  import util.Try

  trait AnySimpleInstructions extends AnyInstructions {

    type Env = AnyFileSystemEnvironment
  }

  abstract class SimpleInstructions[I, O] extends AnySimpleInstructions {

    type In = I
    type Out = O
  }


  case class say[I](msg: String) extends SimpleInstructions[I, I] {

    def run(env: Env, in: In): Result[Out] = Success[I](Seq(msg), in)
  }

  case class fail[I](msg: String) extends SimpleInstructions[I, I] {

    def run(env: Env, in: In): Result[Out] = Failure[I](Seq(msg))
  }


  case class TryInstructions[I, O](tf: I => Try[O]) extends SimpleInstructions[I, O] {

    def run(env: Env, in: In): Result[Out] = {
      tf(in) match {
        case util.Success(output) => Success[O](Seq(tf.toString), output)
        case util.Failure(e) => Failure[O](Seq(e.getMessage))
      }
    }
  }

  implicit def tryToInstructions[I, O](t: Try[O]): TryInstructions[I, O] = TryInstructions[I, O](_ => t)


  case class CmdInstructions[I](sf: I => Seq[String]) extends SimpleInstructions[I, String] {

    def run(env: Env, in: In): Result[Out] = {
      val seq = sf(in)
      Try( Process(seq, env.workingDir).!! ) match {
        case util.Success(output) => Success[String](Seq(seq.mkString(" ")), output)
        case util.Failure(e) => Failure[String](Seq(e.getMessage))
      }
    }
  }

  def cmd[I](command: String)(args: String*): CmdInstructions[I] = CmdInstructions[I](_ => command +: args)

  implicit def seqToInstructions[I](s: Seq[String]): CmdInstructions[I] = CmdInstructions[I](_ => s)


  implicit def instructionsSyntax[X, I <: AnyInstructions](x: X)(implicit toInst: X => AnyInstructions.sameAs[I]):
    InstructionsSyntax[I] =
    InstructionsSyntax[I](toInst(x))

  case class InstructionsSyntax[I <: AnyInstructions](i: AnyInstructions.sameAs[I]) {

    def -&-[U <: AnyInstructions { type In = I#Out; type Env = I#Env }]
      (u: AnyInstructions.sameAs[U]): I -&- U = instructions.-&-[I, U](i, u)
  }

}
