package ohnosequences

import java.io.File
import sys.process._

package object statika {

  implicit def resultSyntax[O](r: Result[O]): ResultSyntax[O] = ResultSyntax[O](r)

  implicit def instructionsSyntax[X, I <: AnyInstructions](x: X)(implicit toInst: X => I):
    InstructionsSyntax[I] =
    InstructionsSyntax[I](toInst(x))

  def say(msg: String): SimpleInstructions[Unit] =
    new SimpleInstructions[Unit]( _ => Success(Seq(msg), ()) )

  def success[O](msg: String, o: O): SimpleInstructions[O] =
    new SimpleInstructions[O]( _ => Success[O](Seq(msg), o) )

  def failure[O](msg: String): SimpleInstructions[O] =
    new SimpleInstructions[O]( _ => Failure[O](Seq(msg)) )

  @SuppressWarnings(Array("org.brianmckenna.wartremover.warts.AsInstanceOf", "org.brianmckenna.wartremover.warts.IsInstanceOf"))
  final def stupidScala[I <: AnyInstructions](i: I): AnyInstructions.sameAs[I] = {

    i.asInstanceOf[AnyInstructions.sameAs[I]]
  }


  def cmd(command: String)(args: String*): CmdInstructions = CmdInstructions(command +: args)
  def process(p: ProcessBuilder): ProcessInstructions = ProcessInstructions(p)
  def withWorkingDir(wd: File)(seq: String*) = CmdWDInstructions(wd)(seq)

  implicit def seqToInstructions(s: Seq[String]): CmdInstructions = CmdInstructions(s)
  implicit def procBuilderToInstructions(p: ProcessBuilder): ProcessInstructions = ProcessInstructions(p)

}
