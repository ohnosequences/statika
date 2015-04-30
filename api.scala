package ohnosequences.statika.aws

import ohnosequences.statika.bundles._
import java.net.URL

object api extends Module {
  
  val statusAWSTag = "statika-status"

  lazy val metadataLocalURL      = new URL("http://169.254.169.254/latest/meta-data")
  lazy val metadataLocalAMIIdURL = new URL(metadataLocalURL, "ami-id")



}