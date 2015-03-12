/*
## Statika package object

This is a package object which mixes other parts of statika, to provide a common namespace, where
all the definitions are accessible.
*/

package ohnosequences

import shapeless._
import shapeless.ops.hlist.Prepend
import ohnosequences.cosas._, typeSets._

package object statika 
 extends ZipUnionHLists with InstallMethods {

  /*  Some context bounds conveniences (`Prepend` not `Merge`!)
      for saying: `[ LM <: HList : (L ::: M)#is ]`
      or `implicit append: (L :+ X)#is[LX]`
  */  
  // type :::[L <: HList, M <: HList] = { type is[O <: HList] = Prepend.Aux[L, M, O] }
  // type :+[L <: HList, X] = { type is[O <: HList] = (L ::: (X :: HNil))#is[O] }
    

  /* ### Implicit conversion from ProcessBuilder-like things to InstallResults */
  implicit def cmdToResult[T : CmdLike](cmd: T): InstallResults = runCommand(cmd)()
  implicit def resultsToList[T : IsResult](r: T): List[InstallResult] = r.trace
  
  implicit class FlattenBundles[Bs <: AnyTypeSet.Of[AnyBundle]](bs: Bs) {
    // def flatten(implicit fl: Flatten[Bs, ∅]): fl.Out = fl(bs, ∅)
    def next(implicit nx: NextLevel[Bs]): nx.Out = nx(bs)
    def levels(implicit ls: Levels[Bs]): ls.Out = ls(bs)
  }
}
