/*
## Amazon Machine Images (AMIs)

This abstraction represents AMIs, that are supposed to be used in distributions
to control, that all the members are installed with the same image.
*/

package ohnosequences.statika.aws

import ohnosequences.statika._, bundles._, instructions._
import scala.util.Try
import java.net.URL

object amis extends Module(api) {


  abstract class AnyAMI extends Environment { ami =>

    val id: String
    val amiVersion: String

    // TODO why not put all the Java/Scala install here?
    /* This method checks that the machine on which it's called has the corresponding image. */
    val instructions: AnyInstructions = {

      try {
        val amiId = io.Source.fromURL(new URL(api.metadataLocalURL, "ami-id")).mkString

        if (amiId == id) say(s"Checked that the Amazon Machine Image id is ${id}")
        else failure(s"AMI should be ${id}. Found ${amiId}")
      } catch {
        case e: Throwable => failure(s"Couldn't check AMI id because of ${e.getMessage}")
      }
    }

  }

  /* A constructor for ami objects */
  abstract class AMI(val id: String, val amiVersion: String) extends AnyAMI

  // no need to add a new trait here
  type AnyAMICompatible = AnyCompatible { type Environment <: AnyAMI }
  type AMICompatible[E <: AnyAMI, B <: AnyBundle] = Compatible[E,B]
}
