
```scala
package ohnosequences.statika

import java.io.File


trait AnyArtifactMetadata {
  val organization: String
  val artifact: String
  val version: String
  val artifactUrl: String
}


trait AnyCompatible {

  val prefixName: String
  val name: String
  lazy val fullName: String = s"${prefixName}.${name}"

  type Environment <: AnyEnvironment
  val  environment: Environment

  type Bundle <: AnyBundle
  val  bundle: Bundle

  val metadata: AnyArtifactMetadata

  // TODO: combining strategy should be an option
  def install: AnyResult = {
    val workingDir = new File(".")

    val allBundles: List[AnyBundle] =
      environment.bundleFullDependencies ++
      bundle.bundleFullDependencies :+
      bundle

    println(allBundles.toString)

    allBundles.foldLeft[AnyResult](
      Success(s"Installing bundle ${bundle.bundleName} with environment ${environment.bundleName}", ())
    ){ (acc, x) =>
      acc match {
        case Failure(tr) => Failure(tr)
        case Success(tr, _) => x.instructions.run(workingDir)
      }
    }
  }

}

abstract class CompatibleWithPrefix[
  E <: AnyEnvironment,
  B <: AnyBundle
](val prefixName: String
)(val environment: E,
  val bundle: B,
  val metadata: AnyArtifactMetadata
) extends AnyCompatible {
  type Me = this.type;
  lazy val me: Me = this: Me

  type Environment = E
  type Bundle = B

  lazy val name: String = this.toString
}

abstract class Compatible[
  E <: AnyEnvironment,
  B <: AnyBundle
](val environment: E,
  val bundle: B,
  val metadata: AnyArtifactMetadata
) extends AnyCompatible {
  type Me = this.type;
  lazy val me: Me = this: Me

  type Environment = E
  type Bundle = B

  lazy val prefixName: String = this.getClass.getName.split("\\$").init.mkString(".")
  lazy val name: String = this.toString
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