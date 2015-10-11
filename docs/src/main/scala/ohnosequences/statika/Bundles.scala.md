
```scala
package ohnosequences.statika
```

## Bundles

A bundle is supposed to be a lightweight cover for any kind of "modules" of any system, such as a
tool (program) installer, data object, code library, etc.

This bundle covering is needed to have control over their dependencies on the level of Scala type
system. So the main part of the bundle is it's dependencies declaration (which is just a typed
set of other bundles).

A bundle can do something valuable, for instance, to install some tool assuming that all it's
dependencies are already installed, or it can deploy some data and provide a convenient interface
for the bundles which are dependent on it. For these purposes, there is an `install` method.


```scala
object bundles {

  import instructions._

  trait AnyBundle {
```

Every bundle has a fully qualified name for distinction

```scala
    lazy val bundleFullName: String = this.getClass.getName.split("\\$").mkString(".")
```

And a short version for convenience

```scala
    lazy val bundleName: String = bundleFullName split('.') last
```

Every bundle has a list of other bundles on which this one is directly dependent

```scala
    val  bundleDependencies: List[AnyBundle]
```

That is used for building a list of all transitive dependencies

```scala
    lazy val bundleFullDependencies: List[AnyBundle] =
      ( ( bundleDependencies flatMap { _.bundleFullDependencies } ) ++ bundleDependencies ).distinct
```

`install` method is what bundle is supposed to do:
- if it's a _tool_, install it;
- if it's a _resource_, prepare/create it (and other methods can provide
  a type-safe interface for interaction with it);
- if it's a _library_, nothing;


```scala
    def install: Results
  }
```

### Auxiliary stuff

  This constructor is convenient, because it takes just a value for the bundle dependencies and sets
  the type-members and evaluates `bundleDependenciesTower`.

  If you want to inherit from this class _abstractly_, you need to preserve the same implicit context and then you can extend it, providing the types explicitly. See [Environment code](Environment.md) for example.


```scala
  abstract class Bundle(d: AnyBundle*) extends AnyBundle { val bundleDependencies = d.toList }
```

A module is just a bundle with an empty install method

```scala
  trait AnyModule extends AnyBundle {

    final def install: Results = success(s"Module ${bundleFullName} is installed")
  }


  abstract class Module(d: AnyModule*) extends AnyModule { val bundleDependencies = d.toList }
```

An environment is a bundle that is supposed to set up some context for other bundles installation

```scala
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

    def install(strategy: InstallStrategy): Results = {

      (environment.bundleFullDependencies ++ bundle.bundleFullDependencies)
        .foldLeft( success(s"Installing bundle ${bundle.bundleName} with environment ${environment.bundleName}") ){
          (res, x) => strategy(res, x.install)
        } -&-
      bundle.install
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

```




[main/scala/ohnosequences/statika/Bundles.scala]: Bundles.scala.md
[main/scala/ohnosequences/statika/Instructions.scala]: Instructions.scala.md
[test/scala/BundleTest.scala]: ../../../../test/scala/BundleTest.scala.md
[test/scala/InstallWithDepsSuite.scala]: ../../../../test/scala/InstallWithDepsSuite.scala.md
[test/scala/InstallWithDepsSuite_Aux.scala]: ../../../../test/scala/InstallWithDepsSuite_Aux.scala.md