
```scala
package ohnosequences.statika

import ohnosequences.statika._

package object aws {

  type AnyLinuxAMICompatible = AnyCompatible { type Environment <: AnyLinuxAMIEnvironment }
  // type AnyAMICompatible = AnyCompatible { type Environment <: AnyAMIEnvironment }
  type AMICompatible[E <: AnyLinuxAMIEnvironment, B <: AnyBundle] = Compatible[E,B]

  implicit def linuxAMICompSyntax[C <: AnyLinuxAMICompatible]
    (comp: C): LinuxAMICompSyntax[C] = LinuxAMICompSyntax(comp)
}

```




[main/scala/ohnosequences/statika/aws/amis.scala]: amis.scala.md
[main/scala/ohnosequences/statika/aws/package.scala]: package.scala.md
[main/scala/ohnosequences/statika/bundles.scala]: ../bundles.scala.md
[main/scala/ohnosequences/statika/compatibles.scala]: ../compatibles.scala.md
[main/scala/ohnosequences/statika/instructions.scala]: ../instructions.scala.md
[main/scala/ohnosequences/statika/package.scala]: ../package.scala.md
[main/scala/ohnosequences/statika/results.scala]: ../results.scala.md
[test/scala/BundleTest.scala]: ../../../../../test/scala/BundleTest.scala.md
[test/scala/InstallWithDepsSuite.scala]: ../../../../../test/scala/InstallWithDepsSuite.scala.md
[test/scala/InstallWithDepsSuite_Aux.scala]: ../../../../../test/scala/InstallWithDepsSuite_Aux.scala.md
[test/scala/instructions.scala]: ../../../../../test/scala/instructions.scala.md