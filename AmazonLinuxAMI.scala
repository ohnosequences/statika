package ohnosequences.statika.aws

import ohnosequences.statika._, bundles._
import ohnosequences.awstools.regions.Region

import ohnosequences.awstools.ec2._
import com.amazonaws.auth.profile._

object amazonLinuxAMIs extends Module(amis, api) {

  import amis._, api._

  /*  Abtract class `AmazonLinuxAMI` provides parts of the user script as it's members, so that
      one can extend it and redefine behaviour, of some part, reusing others.
  */
  abstract class AmazonLinuxAMI(
      val id: String,
      val amiVersion: String
    ) extends AnyAMI { ami =>

    val region: Region
    val arch: Architecture
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
      |    aws ec2 create-tags --region ${region.toString} --resources $$ec2id  --tag Key=${api.statusAWSTag},Value=$$2 > /dev/null
      |  else
      |    echo
      |    echo " -- failure -- "
      |    echo
      |    aws ec2 create-tags --region ${region.toString} --resources $$ec2id  --tag Key=${api.statusAWSTag},Value=failure > /dev/null
      |  fi
      |}
      |
      |cd /root
      |export HOME="/root"
      |export PATH="/root/bin:/opt/aws/bin:$$PATH"
      |export ec2id=$$(curl http://169.254.169.254/latest/meta-data/instance-id)
      |export EC2_HOME=/opt/aws/apitools/ec2
      |export AWS_DEFAULT_REGION=${region.toString}
      |""".stripMargin

    // checks exit code of the previous step
    private def tagStep(state: InstanceStatus): String = s"tagStep $$? ${state}"

    /*  This part should make any necessary for building preparations,
        like installing build tools: java-7 and scala-2.11.6 from rpm's
    */
    private def preparing: String = s"""
      |aws s3 cp --region ${region.toString} s3://resources.ohnosequences.com/scala-2.11.6.rpm scala-2.11.6.rpm
      |yum -y install java-1.8.0-openjdk-devel.x86_64
      |yum -y remove java-1.7.0-openjdk
      |yum -y install scala-2.11.6.rpm
      |""".stripMargin

    /* This is the main part of the script: building applicator. */
    private def building[C <: AnyCompatible](comp: C): String = s"""
      |mkdir -p ${workingDir}
      |cd ${workingDir}
      |
      |echo "object apply extends App { " > apply.scala
      |echo "  val results = ${comp.fullName}.install(ohnosequences.statika.instructions.failFast); " >> apply.scala
      |echo "  results foreach println; " >> apply.scala
      |echo "  if (results.hasFailures) sys.error(results.toString) " >> apply.scala
      |echo "}" >> apply.scala
      |cat apply.scala
      |
      |aws s3 cp --region ${region.toString} ${comp.metadata.artifactUrl} dist.jar
      |
      |scalac -cp dist.jar apply.scala
      |""".stripMargin

    /* Just running what we built. */
    private def applying: String = s"""
      |java -d${arch} -Xmx${javaHeap}G -cp .:dist.jar apply
      |""".stripMargin

    // /* Instance status-tagging. */
    // private def tag(state: InstanceStatus): String = s"""
    //   |echo
    //   |echo " -- ${state} -- "
    //   |echo
    //   |aws ec2 create-tags --region ${region.toString} --resources $$ec2id  --tag Key=${api.statusAWSTag},Value=${state} > /dev/null
    //   |""".stripMargin

    private def fixLineEndings(s: String): String = s.replaceAll("\\r\\n", "\n").replaceAll("\\r", "\n")

    final def userScript[C <: AnyCompatible](comp: C): String =
      fixLineEndings(
        "#!/bin/sh \n" +
        initSetting +
        tagStep(api.preparing) +
        preparing +
        tagStep(api.building) +
        building(comp) +
        tagStep(api.applying) +
        applying +
        tagStep(api.success)
      )

    final def instanceSpecs[C <: AnyCompatible](comp: C)(
      instanceType: InstanceType,
      keyPair: String,
      role: Option[String]
    ): InstanceSpecs =
       InstanceSpecs(
         instanceType = instanceType,
         amiId = id,
         keyName = keyPair,
         userData = userScript(comp),
         instanceProfile = role
       )
  }

  type AnyAmazonLinuxAMICompatible = AnyCompatible { type Environment <: AmazonLinuxAMI }

  implicit def amazonLinuxAMIOps[C <: AnyAmazonLinuxAMICompatible]
    (comp: C): AmazonLinuxAMIOps[C] = AmazonLinuxAMIOps(comp)

  case class AmazonLinuxAMIOps[C <: AnyAmazonLinuxAMICompatible](val comp: C) {
    val ami = comp.environment

    def userScript: String = ami.userScript(comp)

    def instanceSpecs(
      instanceType: InstanceType,
      keyPair: String,
      role: Option[String]
    ): InstanceSpecs = ami.instanceSpecs(comp)(instanceType, keyPair, role)

  }


  // Amazon Linux AMI 2015.03 was released on 2015-03-24
  // ephemeral storage and 64bit
  // See http://aws.amazon.com/amazon-linux-ami/

  object RegionMap {
    import Region._
    def amiId(region: Region): String = region match {
        case NorthernVirginia   => "ami-5ccae734"
        case Oregon             => "ami-97527ea7"
        case NorthernCalifornia => "ami-3714f273"
        case Ireland            => "ami-cf0897b8"
        // case Frankfurt          => "ami-b6221fab"
        case Singapore          => "ami-1cd8e94e"
        case Tokyo              => "ami-d5fa0dd5"
        case Sydney             => "ami-819cecbb"
        case SaoPaulo           => "ami-bf2890a2"
        // case Beijin             => "ami-f439abcd"
        case GovCloud           => "ami-75b2d356"
      }
  }

  case class amzn_ami_pv_64bit(val region: Region)(
    val javaHeap: Int // in G
  ) extends AmazonLinuxAMI(
      id = RegionMap.amiId(region),
      amiVersion = "2015.03"
  ) {

    lazy val arch = x64
    lazy val workingDir = "/media/ephemeral0/applicator"
  }

}

// TODO: make it dependent on instance type and choose PV or HVM
// See http://aws.amazon.com/amazon-linux-ami/instance-type-matrix/
