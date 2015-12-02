/*
## Amazon Machine Images (AMIs)

This abstraction represents AMIs, that are supposed to be used in distributions
to control, that all the members are installed with the same image.
*/

package ohnosequences.statika.aws

import ohnosequences.statika._, bundles._, instructions._, results._
import ohnosequences.awstools._, ec2._
import java.net.URL

abstract class AnyLinuxAMIEnvironment extends Environment {

  type AMI <: AnyLinuxAMI
  val  ami: AMI

  /* This method checks that the machine on which it's called has the corresponding image. */
  def instructions: AnyInstructions = LazyTry[Unit] {
    val amiId = io.Source.fromURL(new URL(ec2.metadataLocalURL, "ami-id")).mkString
    if (amiId == ami.id) Success(s"Checked that the Amazon Machine Image id is ${ami.id}", ())
    else Failure(s"AMI should be ${ami.id}. Found ${amiId}")
  }

  def userScript[C <: AnyCompatible](comp: C): String
}


sealed trait InstanceStatus
case object  InstanceStatus {
  val tag = "statika-status"

  case object preparing extends InstanceStatus
  case object building  extends InstanceStatus
  case object applying  extends InstanceStatus
  case object success   extends InstanceStatus
  case object failure   extends InstanceStatus
}

/*  Abtract class `AmazonLinuxAMI` provides parts of the user script as it's members, so that
    one can extend it and redefine behaviour, of some part, reusing others.
*/
abstract class LinuxAMIEnvironment[
  A <: AnyLinuxAMI
](val ami: A) extends AnyLinuxAMIEnvironment {

  type AMI = A

  val javaHeap: Int // in G
  val workingDir: String

  /*  First of all, `initSetting` part sets up logging.
      Then it sets useful environment variables.
  */
  private def initSetting: String = s"""
    |
    |# redirecting output for logging
    |exec &> /log.txt
    |
    |echo "tail -f /log.txt" > /bin/show-log
    |chmod a+r /log.txt
    |chmod a+x /bin/show-log
    |ln -s /log.txt /root/log.txt
    |
    |function tagStep(){
    |  if [ $$1 = 0 ]; then
    |    echo
    |    echo " -- $$2 -- "
    |    echo
    |    aws ec2 create-tags --region ${ami.region} --resources $$ec2id  --tag Key=${InstanceStatus.tag},Value=$$2 > /dev/null
    |  else
    |    echo
    |    echo " -- failure -- "
    |    echo
    |    aws ec2 create-tags --region ${ami.region} --resources $$ec2id  --tag Key=${InstanceStatus.tag},Value=failure > /dev/null
    |  fi
    |}
    |
    |cd /root
    |export HOME="/root"
    |export PATH="/root/bin:/opt/aws/bin:$$PATH"
    |export ec2id=$$(curl http://169.254.169.254/latest/meta-data/instance-id)
    |export EC2_HOME=/opt/aws/apitools/ec2
    |export AWS_DEFAULT_REGION=${ami.region}
    |""".stripMargin

  // checks exit code of the previous step
  private def tagStep(state: InstanceStatus): String = s"tagStep $$? ${state}"

  /*  This part should make any necessary for building preparations,
      like installing build tools: java-7 and scala-2.11.7 from rpm's
  */
  private def preparing: String = s"""
    |aws s3 cp --region ${ami.region} s3://resources.ohnosequences.com/scala/scala-2.11.7.rpm scala-2.11.7.rpm
    |yum -y remove java-1.7.0-openjdk
    |yum -y install java-1.8.0-openjdk scala-2.11.7.rpm
    |""".stripMargin

  /* This is the main part of the script: building applicator. */
  // TODO: install directory should be configurable
  // FIXME: make device name configurable (and dependent on the instance type)
  private def building[C <: AnyCompatible](comp: C): String = s"""
    |mkdir -p ${workingDir}
    |mkfs -t ext4 /dev/sdb
    |mount /dev/sdb ${workingDir}
    |cd ${workingDir}
    |
    |echo "object apply { " > apply.scala
    |echo "  def main(args: Array[String]): Unit = {" >> apply.scala
    |echo "    val result = ${comp.fullName}.install; " >> apply.scala
    |echo "    result.trace.foreach(println); " >> apply.scala
    |echo "    if (result.hasFailures) sys.error(result.trace.toString) " >> apply.scala
    |echo "  }" >> apply.scala
    |echo "}" >> apply.scala
    |cat apply.scala
    |
    |aws s3 cp --region ${ami.region} ${comp.metadata.artifactUrl} dist.jar
    |
    |scalac -cp dist.jar apply.scala
    |""".stripMargin

  /* Just running what we built. */
  private def applying: String = s"""
    |java -d${ami.arch.wordSize} -Xmx${javaHeap}G -cp .:dist.jar apply
    |""".stripMargin

  private def fixLineEndings(s: String): String = s.replaceAll("\\r\\n", "\n").replaceAll("\\r", "\n")

  final def userScript[C <: AnyCompatible](comp: C): String =
    fixLineEndings(
      "#!/bin/sh \n" +
      initSetting +
      tagStep(InstanceStatus.preparing) +
      preparing +
      tagStep(InstanceStatus.building) +
      building(comp) +
      tagStep(InstanceStatus.applying) +
      applying +
      tagStep(InstanceStatus.success)
    )
}

case class LinuxAMICompSyntax[C <: AnyLinuxAMICompatible](val comp: C) {

  def userScript: String = comp.environment.userScript(comp)

  type AMI = C#Environment#AMI

  def instanceSpecs[
    IT <: AnyInstanceType
  ](instanceType: IT,
    keyPair: String,
    instanceProfile: Option[String],
    securityGroups: List[String] = List(),
    instanceMonitoring: Boolean = false,
    deviceMapping: Map[String, String] = Map[String, String]()
  )(implicit
    supportsAMI: IT SupportsAMI AMI
  ): LaunchSpecs[InstanceSpecs[AMI, IT]] =
    LaunchSpecs(
      InstanceSpecs[AMI, IT](
        comp.environment.ami,
        instanceType
      )(supportsAMI)
    )(keyPair,
      userScript,
      instanceProfile,
      securityGroups,
      instanceMonitoring,
      deviceMapping
    )

}


case class amznAMIEnv[A <: AnyAmazonLinuxAMI](
  amazonAMI: A,
  javaHeap: Int = 1, // in G
  workingDir: String = "/media/ephemeral0/"
) extends LinuxAMIEnvironment[A](amazonAMI)

case object foo {
  val d = amznAMIEnv(AmazonLinuxAMI(regions.Region.Ireland, PV, InstanceStore))
}
