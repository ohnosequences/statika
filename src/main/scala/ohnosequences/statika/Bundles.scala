package ohnosequences.statika

/* ## Bundles

   A bundle is supposed to be a lightweight cover for any kind of "modules" of any system, such as a
   tool (program) installer, data object, code library, etc.

   This bundle covering is needed to have control over their dependencies on the level of Scala type
   system. So the main part of the bundle is it's dependencies declaration (which is just a typed
   set of other bundles).

   A bundle can do something valuable, for instance, to install some tool assuming that all it's
   dependencies are already installed, or it can deploy some data and provide a convenient interface
   for the bundles which are dependent on it. For these purposes, there is an `install` method.
*/

case object bundles {

  import instructions._
  import java.nio.file._

  /* The only thing you have in the beginning of running instrucitons is your environment */
  type AnyBundleInstructions = AnyFileInstructions { type In = Env }

  trait AnyBundle {

    /* Every bundle has a fully qualified name for distinction */
    // NOTE: if you define a bundle inside of an abstract class, this prefix will be wrong
    lazy val bundleFullName: String = this.getClass.getName.split("\\$").mkString(".")

    /* And a short version for convenience */
    lazy val bundleName: String = bundleFullName.split('.').last

    /* Every bundle has a list of other bundles on which this one is directly dependent */
    val  bundleDependencies: List[AnyBundle]

    /* That is used for building a list of all transitive dependencies */
    lazy final val bundleFullDependencies: List[AnyBundle] =
      ( ( bundleDependencies flatMap { _.bundleFullDependencies } ) ++ bundleDependencies ).distinct


    /* Instructions determine the purpuse of the bundle in a declarative form */
    // TODO: should we preserve the instructions type?
    val instructions: AnyBundleInstructions

    // NOTE: this is here to facilate transition to the new API for old bundles
    lazy val env = FileSystemEnvironment(Files.createTempDirectory(Paths.get("."), bundleName).toFile)

    def install: AnyResult = instructions.run(env, env)
  }

  /* ### Auxiliary stuff

  This constructor is convenient, because it takes just a value for the bundle dependencies and sets
  the type-members and evaluates `bundleDependenciesTower`.

  If you want to inherit from this class _abstractly_, you need to preserve the same implicit context and then you can extend it, providing the types explicitly. See [Environment code](Environment.md) for example.
  */
  abstract class Bundle(d: AnyBundle*) extends AnyBundle { val bundleDependencies = d.toList }

  /* A module is just a bundle with an empty install method */
  trait AnyModule extends AnyBundle {

    final val instructions: AnyBundleInstructions = say(s"Module ${bundleFullName} is installed")
  }


  abstract class Module(d: AnyModule*) extends AnyModule { val bundleDependencies = d.toList }


  /* An environment is a bundle that is supposed to set up some context for other bundles installation */
  trait AnyEnvironment extends AnyBundle

  abstract class Environment(d: AnyBundle*) extends AnyEnvironment { val bundleDependencies = d.toList }


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

    lazy val env = FileSystemEnvironment(Files.createTempDirectory(Paths.get("."), fullName).toFile)

    // TODO: combining strategy should be an option
    def instructions: AnyBundleInstructions = {
      val allBundles: List[AnyBundle] =
        environment.bundleFullDependencies ++
        bundle.bundleFullDependencies :+
        bundle

      allBundles.foldLeft[AnyInstructions](
        say(s"Installing bundle ${bundle.bundleName} with environment ${environment.bundleName}")
      ){ (acc, x) =>
        acc -&- x.instructions
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

}
