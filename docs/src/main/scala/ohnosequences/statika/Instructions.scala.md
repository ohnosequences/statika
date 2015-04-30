
```scala
package ohnosequences.statika
```


## Installation utilities

This module defines convenient types for presenting installation results and methods to work with
them.


```scala
object instructions {
```

 ### Install result types

 Basically, result is always either success or failure (with informative message):


```scala
  type FailureMessage = String
  type SuccessMessage = String
  type Result = Either[FailureMessage, SuccessMessage]
```

 As the installation process consists of a sequence of steps, we want to know the result of
 each step. `Results` type is just a cover on a the list of results of each installation
 step. It also contains some operations to combine such steps (i.e. their results).


```scala
  trait Results {

    val trace: List[Result]
    val hasFailures: Boolean
    lazy val isSuccessful: Boolean = ! hasFailures
```

 Combinators:
 * `A ->- B` — "and then": if `A` was successful, return `B`
 * `A -&- B` — "and": if `A` was successful, append it to `B`
 * `A -|- B` — "or": irrespectively of `A` result, append it to `B`

 Note that the second argument is lazy and it can be anything that can be converted to
 `Results`.


```scala
    def ->-[T : IsResult](ts: => T): Results
    def -&-[T : IsResult](ts: => T): Results
    def -|-[T : IsResult](ts: => T): Results

  }

  // Type alias for context bounds (instead of view bounds)
  type IsResult[T] = T => Results
```

 Type alias which can be used for folding of install results. Particularly, it is used in the
 `Distribution` method `installWithDeps`, as the way of traversing the list of dependencies is
 important there.


```scala
  type InstallStrategy = (Results, Results) => Results
  val failFast:     InstallStrategy = _ -&- _
  val failTolerant: InstallStrategy = _ -|- _
```

 Now, we want not just to operate on a list of results, but to have an indicator of how things
 are going for the whole list. `Success` represents a list, where all results are positive and
 defines the appropriate combinators:


```scala
  case class Success(val trace: List[Result]) extends Results {
    
    val hasFailures = false

    def ->-[T : IsResult](ts: => T) = ts
    def -&-[T : IsResult](ts: => T) = implicitly[Results](ts) match {
      case Success(tr) => Success(trace ::: tr)
      case Failure(tr) => Failure(trace ::: tr)
    }
    def -|-[T : IsResult](ts: => T) = Success(trace ::: ts.trace)
  }
```

`Failure` represents a list of results, among which there is at least one negative:

```scala
  case class Failure(val trace: List[Result]) extends Results {
    
    val hasFailures = true

    def ->-[T : IsResult](ts: => T) = this
    def -&-[T : IsResult](ts: => T) = this
    def -|-[T : IsResult](ts: => T) = implicitly[Results](ts) match {
      case Success(tr) => Success(trace ::: tr)
      case Failure(tr) => Failure(trace ::: tr)
    }
  }
```

 For backwards compatibility and for convenience, there are simple "constructors" for the
 `Results` instances. You can think of `Results` just as about _lists of
 messages_ and as they are (implicitly) convertible to `List`, use all normal list operations
 on them. But be careful: if you combine them, better use the predefined combinators: `->-`,
 `-&-` and `-|-`, as they will preserve the sense of the installation process overall result.


```scala
  def success(msg: SuccessMessage): Results = Success(List(Right(msg)))
  def failure(msg: FailureMessage): Results = Failure(List(Left(msg)))
```

### Install results of external commands

```scala
  import sys.process._
  import java.io.File

  // Adding method to run commands from a given path
  implicit class SeqCWD(val cmd: Seq[String]) extends AnyVal {
    def @@(path: File) = Process(cmd, path, "" -> "")
  }
  implicit class StrCWD(val cmd: String) extends AnyVal {
    def @@(path: File) = Process(cmd, path, "" -> "")
  }
```

 Conversion from commands (`Process`) (or anything that can be treated as a command) to install
 results allows us to write concise and readable code: you can use `String`s or `Seq[String]`
 for expressing commands and combine them with `->-`, `-&-` or `-|-` and you don't need
 manually collect results of running each command, checking it's exit code and setting
 appropriate install result method for it — you just combine them and get informative trace of
 the run process as an output.


```scala
  type CmdLike[T] = T => ProcessBuilder

  def runCommand[T : CmdLike](cmd: T)(
      failureMsg: String = cmd.toString
    , successMsg: String = cmd.toString
  ): Results = {
    if(cmd.! == 0) success(successMsg)
    else failure(failureMsg)
  }
```

### Implicit conversion from ProcessBuilder-like things to Results

```scala
  implicit def cmdToResult[T : CmdLike](cmd: T): Results = runCommand(cmd)()
  implicit def resultsToList[T : IsResult](r: T): List[Result] = r.trace

}

```


------

### Index

+ src
  + test
    + scala
      + [InstallWithDepsSuite_Aux.scala][test/scala/InstallWithDepsSuite_Aux.scala]
      + [InstallWithDepsSuite.scala][test/scala/InstallWithDepsSuite.scala]
      + [BundleTest.scala][test/scala/BundleTest.scala]
  + main
    + scala
      + ohnosequences
        + statika
          + [Bundles.scala][main/scala/ohnosequences/statika/Bundles.scala]
          + [Instructions.scala][main/scala/ohnosequences/statika/Instructions.scala]

[test/scala/InstallWithDepsSuite_Aux.scala]: ../../../../test/scala/InstallWithDepsSuite_Aux.scala.md
[test/scala/InstallWithDepsSuite.scala]: ../../../../test/scala/InstallWithDepsSuite.scala.md
[test/scala/BundleTest.scala]: ../../../../test/scala/BundleTest.scala.md
[main/scala/ohnosequences/statika/Bundles.scala]: Bundles.scala.md
[main/scala/ohnosequences/statika/Instructions.scala]: Instructions.scala.md