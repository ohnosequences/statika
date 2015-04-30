/*
## Amazon Machine Images (AMIs)

This abstraction represents AMIs, that are supposed to be used in distributions
to control, that all the members are installed with the same image.
*/

package ohnosequences.statika.aws

import ohnosequences.statika._, bundles._, instructions._

object amis extends Module(api) {


  abstract class AnyAMI extends Environment() { ami =>

    val id: String
    val amiVersion: String

    /*  This is the main purpose of having this image abstraction: to be able to generate a
        user-script for a particular bundle, using which can launch an instance or an
        auto-scaling group with this bundle being installed (what is called to _apply a bundle_).
        - it requires distribution metadata, because it needs to fill placeholders in the project of
          the so called "bundle applicator" project;
        - the given bundle must be a member of the given distribution and must have all the
          necessary implicits for being installed with it;
        - for info about credentials see the definition of `AWSCredentials` type;
    */
    def userScript[B <: AnyBundle, E >: ami.type <: AnyEnvironment]
      (bundle: B)(implicit comp: E => Compatible[E, B]): String

    /* This method checks that the machine on which it's called has the corresponding image. */
    def install: Results = {
      import java.io._
      import java.net.URL

      try {
        
        val amiId = io.Source.fromURL(api.metadataLocalAMIIdURL).mkString

        if (amiId == id)
          success("Checked that Amazon Machine Image is " + id)
        else
          failure("AMI should be "+ id +", instead of "+ ami)
          
      } catch {
        case t : Throwable => failure("Couldn't check AMI id because of "+t.toString)
      }
    }

  }

  /* A constructor for ami objects */
  abstract class AMI(val id: String, val amiVersion: String) extends AnyAMI

  // no need to add a new trait here
  type AMICompatible[E <: AnyAMI, B <: AnyBundle] = Compatible[E,B]
}