
```scala
package ohnosequences.statika


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

```




[main/scala/ohnosequences/statika/aws/amis.scala]: aws/amis.scala.md
[main/scala/ohnosequences/statika/aws/package.scala]: aws/package.scala.md
[main/scala/ohnosequences/statika/bundles.scala]: bundles.scala.md
[main/scala/ohnosequences/statika/compatibles.scala]: compatibles.scala.md
[main/scala/ohnosequences/statika/instructions.scala]: instructions.scala.md
[main/scala/ohnosequences/statika/package.scala]: package.scala.md
[main/scala/ohnosequences/statika/results.scala]: results.scala.md
[test/scala/BundleTest.scala]: ../../../../test/scala/BundleTest.scala.md
[test/scala/InstallWithDepsSuite.scala]: ../../../../test/scala/InstallWithDepsSuite.scala.md
[test/scala/InstallWithDepsSuite_Aux.scala]: ../../../../test/scala/InstallWithDepsSuite_Aux.scala.md
[test/scala/instructions.scala]: ../../../../test/scala/instructions.scala.md