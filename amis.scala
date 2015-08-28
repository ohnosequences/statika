/*
## Amazon Machine Images (AMIs)

This abstraction represents AMIs, that are supposed to be used in distributions
to control, that all the members are installed with the same image.
*/

package ohnosequences.statika.aws

import ohnosequences.statika._, bundles._, instructions._, results._
import scala.util.Try
import java.net.URL

object amis extends Module(api) {


  abstract class AnyAMI extends Environment { ami =>

    val id: String
    val amiVersion: String

    // TODO why not put all the Java/Scala install here?
    /* This method checks that the machine on which it's called has the corresponding image. */
    def instructions: AnyInstructions = LazyTry[Unit] {
      println("FOOOO")
      val amiId = io.Source.fromURL(new URL(api.metadataLocalURL, "ami-id")).mkString
      if (amiId == id) Success(s"Checked that the Amazon Machine Image id is ${id}", ())
      else Failure(s"AMI should be ${id}. Found ${amiId}")
    }

  }

  /* A constructor for ami objects */
  abstract class AMI(val id: String, val amiVersion: String) extends AnyAMI

  // no need to add a new trait here
  type AnyAMICompatible = AnyCompatible { type Environment <: AnyAMI }
  type AMICompatible[E <: AnyAMI, B <: AnyBundle] = Compatible[E,B]
}
