/*
## Distributions concept

The basic idea behind a distribution is to group a set of bundles that are published for the 
runtime environment configuration, together with all the underlying infrastructure needed by 
statika to work with it.

This "background stuff" (things like resolvers, artifact - bundle mappings, etc) is essential for
the correct functioning of statika; the goal here is to abstract over it and bring it to the
foreground, giving ways of providing it through code.

Here is the abstract part of distribution, while sbt-specific settings are defined at [ohnosequences
/sbt-statika](https://github.com/ohnosequences/sbt-statika) and the AWS-specific part is added in
[ohnosequences/aws-statika](https://github.com/ohnosequences/aws-statika) package.

Summing the following code up, a distribution consists of
- a set of _members_ (bundles) that are known to work with this distribution;
- _resources management_ interface (_in developement_);
- methods for correct bundles _installation_ with respect to their dependencies;
*/

package ohnosequences.statika

import shapeless._
import shapeless.poly._
import shapeless.ops.hlist._
import ohnosequences.typesets._

trait AnyDistribution extends AnyBundle { dist =>

  /*  ### Distribution members

      Basically it's just a set of bundles that _work with this distribution_.
  */
  type Members <: TypeSet
  val  members: Members

  type isMember[B <: AnyBundle] = B ∈ Members


  /*  ### Installation methods

      These are methods, which are related to organizing installation of bundles with their
      dependencies and the correct environment.

      To map over an `HList` of bundles, we need a shapeless-style poly-function that calls install
      method of each bundle providing him this distribution as a parameter.

      Distribution may need to set some context/do checks before installation. So this method will 
      be called before any installation process.
  */
  def setContext: InstallResults

  /*  Now, the main part: installing a bundle with it's dependencies. This method installs first 
      all the dependencies of the bundle by map-folding it's `depsTower` with `Install` 
      poly-function. Default strategy is `failFast`, which means, that if something in the 
      sequence failed to install, the process stops and returns the trace of installation steps.
  */  
  type isInstallableList[Bs <: HList] = MapFolder[Bs, InstallResults, Install.type]
  type isInstallableSet[S <: TypeSet] = SetMapFolder[S, InstallResults, Install.type]
  type isInstallable[B <: AnyBundle] = isInstallableList[B#DepsTower]
 
  object Install extends Poly1 {
      implicit def bundle[B <: AnyBundle] =
        at[B]{ _.install(dist) }
 
      implicit def typeset[S <: TypeSet : isInstallableSet] =
        at[S]{ _.mapFold(accum)(Install)(failFast) }

      implicit def hlist[L <: HList : isInstallableList] =
        at[L]{ _.foldMap(accum)(Install)(failFast) }

      // This is an accumulator for the map-folders
      val accum = Success(List()) : InstallResults
  }
 
  def installWithDeps[B <: AnyBundle : isMember : isInstallable](b: B): InstallResults =
      setContext -&- Install((b: B).depsTower) -&- Install(b)

}

/* 
### Auxiliary stuff

Just a constructor with the parameters for members and deps:
*/
abstract class Distribution[
    M <: TypeSet : ofBundles
  , D <: TypeSet : ofBundles
  , T <: HList   : towerFor[D]#is
  ](val  members:  M, deps: D = ∅) extends Bundle[D, T](deps) with AnyDistribution {
    type Members = M 
}
