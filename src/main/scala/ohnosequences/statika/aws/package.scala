package ohnosequences.statika

import ohnosequences.statika.bundles._

package object aws {

  type AnyLinuxAMICompatible = AnyCompatible { type Environment <: AnyLinuxAMIEnvironment }
  // type AnyAMICompatible = AnyCompatible { type Environment <: AnyAMIEnvironment }
  type AMICompatible[E <: AnyLinuxAMIEnvironment, B <: AnyBundle] = Compatible[E,B]

  implicit def linuxAMICompSyntax[C <: AnyLinuxAMICompatible]
    (comp: C): LinuxAMICompSyntax[C] = LinuxAMICompSyntax(comp)
}
