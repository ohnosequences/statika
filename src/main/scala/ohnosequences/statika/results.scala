package ohnosequences.statika.results


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


case class ResultSyntax[O](r: Result[O]) {
  def +:(msgs: Seq[String]): Result[O] = r.prependTrace(msgs)
}
