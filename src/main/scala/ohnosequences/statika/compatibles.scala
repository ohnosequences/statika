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
