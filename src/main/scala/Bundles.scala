package ohnosequences.statika

/*
## Bundles

This is the heart of Statika library. Bundles... 

A bundle is supposed to be a lightweight cover for any kind of "modules" of any system, such as a 
tool (program) installer, data object, code library, etc.

This bundle covering is needed to have control on their dependencies on the level of Scala type 
system. So the main part of the bundle is it's dependencies declaration (which is just a set of
other bundles).

A bundle can do something valuable, for instance, to install some tool assuming that all it's
dependencies are already installed, or it can deploy some data and provide a convenient interface
for the bundles which are dependent on it. For these purposes, there is an `install` method. All 
the work with environment should be done there.
*/

object bundles {

  import installations._
  import environments._

  import ohnosequences.cosas._, typeSets._
  import ohnosequences.cosas.ops.typeSets._

  trait AnyBundle {

    final val fullName: String = this.getClass.getName.split("\\$").mkString(".")
    final val name: String = fullName.split('.').last
    
    /* Every bundle has a list of other bundles on which this one is dependent */
    type Deps <: AnyTypeSet.Of[AnyBundle]
    val  deps: Deps

    val depsList: List[AnyBundle]
    lazy val flattenDeps: List[AnyBundle] = {
      (depsList.flatMap{ _.flattenDeps } ::: depsList).distinct
    }

    /*  `install` method is what bundle is supposed to do:
        - if it's a _tool_, install it;
        - if it's a _resource_, prepare/create it (and other methods can provide 
          a type-safe interface for interaction with it);
        - if it's a _library_, nothing;

        So this method contains any bundle's interaction with the environment and that's why it
        requires a distribution (see also `Environment.scala`).
    */
    def install[D <: AnyEnvironment](d: D): InstallResults
  }

  /* ### Auxiliary stuff

  This constructor is convenient, because it takes just a value for the bundle dependencies and sets 
  the type-members and evaluates `depsTower`.

  If you want to inherit from this class _abstractly_, you need to preserve the same implicit context and then you can extend it, providing the types explicitly. See [Environment code](Environment.md) for example.
  */
  abstract class Bundle[Ds <: AnyTypeSet.Of[AnyBundle]]
    (val  deps:  Ds = ∅)
    (implicit val getDepsList: ToList[Ds] { type Out = List[AnyBundle] }) 
      extends AnyBundle {

    type Deps = Ds 

    val depsList = getDepsList(deps)
  }


  /* A module is just a bundle with an empty install method */
  trait AnyModule extends AnyBundle {

    type Deps <: AnyTypeSet.Of[AnyModule]

    def install[D <: AnyEnvironment](d: D): InstallResults = success(name + " is installed")
  }


  abstract class Module[Ds <: AnyTypeSet.Of[AnyModule]]
    (val  deps:  Ds = ∅)
    (implicit val getDepsList: ToList[Ds] { type Out = List[AnyBundle] }) 
      extends AnyModule {

    type Deps = Ds 

    val depsList = getDepsList(deps)
  }


  class Compatible[B <: AnyBundle, E <: AnyEnvironment](b: B, e: E)

  trait AnyEnvironment {

    final val fullName: String = this.getClass.getName.split("\\$").mkString(".")
    final val name: String = fullName.split('.').last

    // type Deps <: AnyTypeSet.Of[AnyEnvironment]

    /* Environment may need to set some context/do checks before installation. So this method will
       be called before any installation process.
    */
    def setContext: InstallResults
  }


  implicit def bundleOps[B <: AnyBundle](b: B):
        BundleOps[B] =
    new BundleOps[B](b)
  class BundleOps[B <: AnyBundle](b: B) {

    def installWithEnv[E <: AnyEnvironment](env: E)(implicit check: Compatible[B, E]) = {
      b.flattenDeps.foldLeft( env.setContext ){ 
        (res, x) => res -&- x.install(env) 
      } -&- 
      b.install(env)
    }
  }


}
