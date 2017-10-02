/*
## Amazon Machine Images (AMIs)

This abstraction represents AMIs, that are supposed to be used in distributions
to control, that all the members are installed with the same image.
*/

package ohnosequences.statika.aws

import ohnosequences.statika._
import ohnosequences.awstools._, ec2._
import java.io.File

abstract class AnyLinuxAMIEnvironment extends Environment {

  type AMI <: AnyLinuxAMI
  val  ami: AMI

  /* This method checks that the machine on which it's called has the corresponding image. */
  def instructions: AnyInstructions = LazyTry[Unit] {
    val amiId = getLocalMetadata("ami-id")
    if (amiId == util.Success(ami.id)) Success(s"Checked that the Amazon Machine Image id is ${ami.id}", ())
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

  val scalaVersion: String

  val javaHeap: Int // in G
  val javaOptions: Seq[String]
  val workingDir: File

  val logFile: Option[File]

  def logRedirect: String = logFile.map { file => s"exec &> ${file.getAbsolutePath}" }.getOrElse("")

  /*  First of all, `initSetting` part sets up logging.
      Then it sets useful environment variables.
  */
  private def initSetting: String = logRedirect ++ s"""
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
      like installing build tools: java-8 and scala from rpm's
  */
  private def preparing: String = s"""
    |aws s3 cp --region ${ami.region} s3://resources.ohnosequences.com/scala/scala-${scalaVersion}.rpm scala-${scalaVersion}.rpm
    |yum -y remove java-1.7.0-openjdk
    |yum -y install java-1.8.0-openjdk scala-${scalaVersion}.rpm
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
    |java -d${ami.arch.wordSize} -Xmx${javaHeap}G ${javaOptions.mkString(" ")} -cp .:dist.jar apply
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

  // type AMI = C#Environment#AMI
  type AMI = comp.environment.AMI

  def instanceSpecs[
    IT <: AnyInstanceType
  ](instanceType: IT,
    keyPair: String,
    instanceProfile: Option[String],
    securityGroups: Set[String] = Set(),
    instanceMonitoring: Boolean = false,
    deviceMapping: Map[String, String] = Map[String, String]()
  )(implicit
    supportsAMI: IT SupportsAMI AMI
  ): LaunchSpecs[IT, AMI] =
    LaunchSpecs(
      comp.environment.ami,
      instanceType,
      keyPair,
      userScript,
      instanceProfile,
      instanceMonitoring,
      securityGroups,
      deviceMapping
    )(supportsAMI)

}


case class amznAMIEnv[A <: AnyAmazonLinuxAMI](
  amazonAMI: A,
  scalaVersion: String = "2.11.11",
  workingDir: File = new File("/media/ephemeral0/"),
  javaHeap: Int = 1, // in G
  javaOptions: Seq[String] = Seq()
) extends LinuxAMIEnvironment[A](amazonAMI) {

  val logFile = Some(new File("/log.txt"))
}
