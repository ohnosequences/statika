### Index

+ src
  + main
    + scala
      + [Bundle.scala](../../main/scala/Bundle.md)
      + [DepsTower.scala](../../main/scala/DepsTower.md)
      + [Distribution.scala](../../main/scala/Distribution.md)
      + [InstallMethods.scala](../../main/scala/InstallMethods.md)
      + [package.scala](../../main/scala/package.md)
      + [ZipUnionHLists.scala](../../main/scala/ZipUnionHLists.md)
  + test
    + scala
      + [BundleTest.scala](BundleTest.md)
      + [CompilationTimeTest.scala](CompilationTimeTest.md)
      + [FlattenVsPoly.scala](FlattenVsPoly.md)
      + [FlattenVsPoly_Aux.scala](FlattenVsPoly_Aux.md)
      + [InstallWithDepsSuite.scala](InstallWithDepsSuite.md)
      + [InstallWithDepsSuite_Aux.scala](InstallWithDepsSuite_Aux.md)

------

package ohnosequences.statika.tests

import ohnosequences.statika._

class CompilationTimeSuite extends org.scalatest.FunSuite {

  case object B1_wqyAM extends Bundle("B1_wqyAM")
  case object B1_drFkc extends Bundle("B1_drFkc")
  case object B1_Hrblr extends Bundle("B1_Hrblr")
  case object B2_g03Hj extends Bundle("B2_g03Hj")
  case object B2_upP5J extends Bundle("B2_upP5J", B1_wqyAM :~: B1_Hrblr :~: ∅)
  case object B3_jBdem extends Bundle("B3_jBdem", B1_wqyAM :~: B2_g03Hj :~: B2_upP5J :~: ∅)
  case object B3_OcpBv extends Bundle("B3_OcpBv", B1_wqyAM :~: B1_drFkc :~: B1_Hrblr :~: B2_g03Hj :~: B2_upP5J :~: ∅)
  case object B3_5qczx extends Bundle("B3_5qczx", B1_wqyAM :~: B1_drFkc :~: B1_Hrblr :~: B2_upP5J :~: ∅)
  case object B3_avkyF extends Bundle("B3_avkyF", B2_g03Hj :~: ∅)

  // 3 levels: 11 10 10 10 10 
  // 3 levels: 16 12 9 9 8
  // 3 levels: 20 19 23 16 16 | 12 10 | 9 9 9 | 7 14 12 10 9 (sets)

  case object B4_prxyi extends Bundle("B4_prxyi", B1_drFkc :~: B2_g03Hj :~: ∅)
  case object B4_j1nxz extends Bundle("B4_j1nxz", B1_drFkc :~: B1_Hrblr :~: B2_g03Hj :~: B2_upP5J :~: B3_avkyF :~: ∅)
  case object B4_samyq extends Bundle("B4_samyq", B1_wqyAM :~: B1_drFkc :~: B1_Hrblr :~: B2_g03Hj :~: B2_upP5J :~: B3_jBdem :~: B3_OcpBv :~: B3_5qczx :~: B3_avkyF :~: ∅)
  case object B4_fluwr extends Bundle("B4_fluwr", B1_drFkc :~: ∅)

  // 4 levels: 32 31 32 30 30
  // 4 levels: 13 16 15 15 16
  // 4 levels: 39 24 22 | 21 20 20 20 | 19 18 19 19 | 18 16 17 15 (sets)


  case object B5_z7fmn extends Bundle("B5_z7fmn", B1_wqyAM :~: B1_Hrblr :~: B2_g03Hj :~: B2_upP5J :~: B3_jBdem :~: B3_5qczx :~: B4_prxyi :~: B4_j1nxz :~: B4_samyq :~: B4_fluwr :~: ∅)
  case object B5_mfRau extends Bundle("B5_mfRau", B1_wqyAM :~: B1_drFkc :~: B2_g03Hj :~: B3_OcpBv :~: B3_avkyF :~: B4_samyq :~: ∅)
  case object B5_g6itc extends Bundle("B5_g6itc", B4_prxyi :~: B4_samyq :~: ∅)
  case object B5_jl9qy extends Bundle("B5_jl9qy", B1_wqyAM :~: B1_drFkc :~: B1_Hrblr :~: B2_g03Hj :~: B2_upP5J :~: B3_jBdem :~: B3_OcpBv :~: B3_5qczx :~: B4_prxyi :~: B4_j1nxz :~: B4_samyq :~: B4_fluwr :~: ∅)
  case object B5_7lznq extends Bundle("B5_7lznq", B1_drFkc :~: B1_Hrblr :~: B2_g03Hj :~: B2_upP5J :~: B3_jBdem :~: B3_OcpBv :~: B3_5qczx :~: B3_avkyF :~: B4_prxyi :~: B4_j1nxz :~: B4_samyq :~: B4_fluwr :~: ∅)

  // 5 levels: 148 182
  // 5 levels: 37 40 35 33 35
  // 5 levels: 57 51 53 | 53 53 47 50 | 48 51 48 49 46 | 56 50 62 53 53 (sets)


  case object Bs extends Bundle("Bs", B1_wqyAM :~: B1_drFkc :~: B1_Hrblr :~: B2_g03Hj :~: B2_upP5J :~: B3_jBdem :~: B3_OcpBv :~: B3_5qczx :~: B3_avkyF 
    // :~: B4_prxyi :~: B4_j1nxz :~: B4_samyq :~: B4_fluwr 
    // :~: B5_z7fmn :~: B5_mfRau :~: B5_g6itc :~: B5_jl9qy :~: B5_7lznq 
    :~: ∅)

// case object BsDist extends Distribution(Bs :~: ∅) {
//   def setContext = success("Distribution " + metadata.name)
//   def install[D <: AnyDistribution](dist: D) = success(metadata.name)
// }

  test("Dependencies by levels") {
    Bs.depsTower.map(generalPrintln)
  }
}

