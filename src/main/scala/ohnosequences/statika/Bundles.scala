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

object bundles {

  import instructions._

  import ohnosequences.cosas._, typeSets._
  import ohnosequences.cosas.ops.typeSets._


  trait AnyBundle {

    /* Every bundle has a fully qualified name for distinction */
    final val fullName: String = this.getClass.getName.split("\\$").mkString(".")

    /* And a short version for convenience */
    final val name: String = fullName.split('.').last

    /* Every bundle has a list of other bundles on which this one is directly dependent */
    type Deps <: AnyTypeSet.Of[AnyBundle]
    val  deps: Deps

    /* This is just an untyped version of the dependencies */
    val depsList: List[AnyBundle]

    /* That is used for building a list of all transitive dependencies */
    lazy val fullDeps: List[AnyBundle] = (depsList.flatMap{ _.fullDeps } ::: depsList).distinct

    /* `install` method is what bundle is supposed to do:
       - if it's a _tool_, install it;
       - if it's a _resource_, prepare/create it (and other methods can provide
         a type-safe interface for interaction with it);
       - if it's a _library_, nothing;
    */
    def install: Results
  }

  /* ### Auxiliary stuff

  This constructor is convenient, because it takes just a value for the bundle dependencies and sets
  the type-members and evaluates `depsTower`.

  If you want to inherit from this class _abstractly_, you need to preserve the same implicit context and then you can extend it, providing the types explicitly. See [Environment code](Environment.md) for example.
  */
  abstract class Bundle[Ds <: AnyTypeSet.Of[AnyBundle]]
   (val deps:  Ds)
   (implicit val getDepsList: ToList[Ds] { type Out = List[AnyBundle] })
      extends AnyBundle {

    type Deps = Ds

    lazy val depsList = getDepsList(deps)
  }


  /* A module is just a bundle with an empty install method */
  trait AnyModule extends AnyBundle {

    type Deps <: AnyTypeSet.Of[AnyModule]

    final def install: Results = success(s"Module ${fullName} is installed")
  }


  abstract class Module[Ds <: AnyTypeSet.Of[AnyModule]]
    (val deps: Ds)
    (implicit val getDepsList: ToList[Ds] { type Out = List[AnyModule] })
      extends AnyModule {

    type Deps = Ds

    lazy val depsList = getDepsList(deps)
  }


  /* An environment is a bundle that is supposed to set up some context for other bundles installation */
  trait AnyEnvironment extends AnyBundle {

    type Deps <: AnyTypeSet.Of[AnyBundle]
  }

  abstract class Environment[Ds <: AnyTypeSet.Of[AnyBundle]]
    (val deps: Ds)
    (implicit val getDepsList: ToList[Ds] { type Out = List[AnyBundle] })
      extends AnyEnvironment {

    type Deps = Ds

    lazy val depsList = getDepsList(deps)
  }


  /* In order to install some bundle with its deps, one should declare that this bundle is
     compatible with a particular environment and then use the `installWithEnv` method */
  class Compatible[B <: AnyBundle, E <: AnyEnvironment](b: B, e: E)

  implicit def bundleOps[B <: AnyBundle](b: B):
        BundleOps[B] =
    new BundleOps[B](b)
  class BundleOps[B <: AnyBundle](b: B) {

    def installWithEnv[E <: AnyEnvironment]
      (env: E, strategy: InstallStrategy)
      (implicit check: Compatible[B, E]): Results = {

      (env.fullDeps ++ b.fullDeps)
        .foldLeft( success(s"Installing bundle ${b.name} with environment ${env.name}") ){
          (res, x) => strategy(res, x.install)
        } -&-
      b.install

    }
  }

}
