/*
## Bundles

This is the heart of Statika library. Bundles... 

A bundle is supposed to be a lightweight cover for any kind of "modules" of any system, such as a tool (program) installer, data object, code library, etc.

This bundle covering is needed to have control on their dependencies on the level of Scala type 
system. So the main part of the bundle is it's dependencies declaration (which is just a set of
other bundles).

A bundle can do something valuable, for instance, to install some tool assuming that all it's
dependencies are already installed, or it can deploy some data and provide a convenient interface
for the bundles which are dependent on it. For these purposes, there is an `install` method. All 
the work with environment should be done there.
*/

package ohnosequences.statika

import shapeless._
import ohnosequences.typesets._

trait AnyBundle {

  final val fullName: String = this.getClass.getName.split("\\$").mkString(".")
  final val name: String = fullName.split('.').last
  
  /* Every bundle has a list of other bundles on which this one is dependent */
  type Deps <: TypeSet
  val  deps: Deps

  /*  Besides the list of top-level dependencies (`deps`), a bundle should know all indirect
      dependencies, because it will need them for the proper installation. So the `depsTower` value
      stores _all_ dependencies of the bundle, but in a format of a **tower**.

      It's an `HList` of `HList`s: first goes the list of bundles which don't depend  on anything
      _0-level_, then bundles, which are dependent on them — _1-level_,  then bundles that can
      depend on 0-level or 1-level and so on.

      So one can evaluate the level of bundle as length of `depsTower` plus one.
  */
  type DepsTower <: HList
  val  depsTower: DepsTower

  /*  `install` method of `Bundle` is what bundle is supposed to do:
      - if it's a _tool_, install it;
      - if it's a _resource_, prepare/create it (and other methods can provide 
        a type-safe interface for interaction with it);
      - if it's a _library_, nothing;

      So this method contains any bundle's interaction with the environment and that's why it
      requires a distribution (see also `Distribution.scala`).
  */
  def install[D <: AnyDistribution](d: D): InstallResults = success(name + " is installed")
}

/* ### Auxiliary stuff

This constructor is convenient, because it takes just a value for the bundle dependencies and sets 
the type-members and evaluates `depsTower`.

If you want to inherit from this class _abstractly_, you need to preserve the same implicit context and then you can extend it, providing the types explicitly. See [Distribution code](Distribution.md) for example.
*/
abstract class Bundle[
    D <: TypeSet : ofBundles
  , T <: HList   : towerFor[D]#is
  ](val  deps:  D = ∅) extends AnyBundle {
    type Deps = D 
    val  depsTower = deps.tower
    type DepsTower = T
  }

