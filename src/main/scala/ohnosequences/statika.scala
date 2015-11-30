package ohnosequences

import ohnosequences.statika._, instructions._, results._

package object statika {

  implicit def resultSyntax[O](r: Result[O]): ResultSyntax[O] = ResultSyntax[O](r)

  implicit def instructionsSyntax[X, I <: AnyInstructions](x: X)(implicit toInst: X => I):
    InstructionsSyntax[I] =
    InstructionsSyntax[I](toInst(x))


  @SuppressWarnings(Array("org.brianmckenna.wartremover.warts.AsInstanceOf", "org.brianmckenna.wartremover.warts.IsInstanceOf"))
  final def stupidScala[I <: AnyInstructions](i: I): AnyInstructions.sameAs[I] = {

    i.asInstanceOf[AnyInstructions.sameAs[I]]
  }


  def cmd(command: String)(args: String*): CmdInstructions = CmdInstructions(command +: args)
  implicit def seqToInstructions(s: Seq[String]): CmdInstructions = CmdInstructions(s)

}
