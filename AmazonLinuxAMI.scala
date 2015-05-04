package ohnosequences.statika.aws

import ohnosequences.statika._, bundles._
import ohnosequences.awstools.regions.Region

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
    def initSetting: String = """
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
      |  if [ $1 = 0 ]; then
      |    $tagOk$
      |  else
      |    $tagFail$
      |  fi
      |}
      |
      |cd /root
      |export HOME="/root"
      |export PATH="/root/bin:/opt/aws/bin:$PATH"
      |export ec2id=$(curl http://169.254.169.254/latest/meta-data/instance-id)
      |export EC2_HOME=/opt/aws/apitools/ec2
      |export AWS_DEFAULT_REGION=$region$
      |""".stripMargin.
        replace("$region$", s"${region}").
        replace("$tagOk$", tag(api.preparing)).
        replace("$tagFail$", tag(api.failure))

    /*  This part should make any necessary for building preparations,
        like installing build tools: java-7 and scala-2.11.6 from rpm's
    */
    def preparing: String = """
      |aws s3 cp s3://resources.ohnosequences.com/scala-2.11.6.rpm scala-2.11.6.rpm
      |yum -y install java-1.8.0-openjdk-devel.x86_64
      |yum -y remove java-1.7.0-openjdk
      |yum -y install scala-2.11.6.rpm
      |""".stripMargin

    /* This is the main part of the script: building applicator. */
    def building[B <: AnyBundle](
        bundle: B,
        metadata: AnyArtifactMetadata
      ): String = s"""
      |mkdir -p ${workingDir}
      |cd ${workingDir}
      |
      |echo "object apply extends App { " > apply.scala
      |echo "  val results = ${bundle.bundleFullName}.installWithEnv(${ami.bundleFullName}, ohnosequences.statika.instructions.failFast); " >> apply.scala
      |echo "  results foreach println; " >> apply.scala
      |echo "  if (results.hasFailures) sys.error(results.toString) " >> apply.scala
      |echo "}" >> apply.scala
      |cat apply.scala
      |
      |aws s3 cp ${metadata.artifactUrl} dist.jar
      |
      |scalac -cp dist.jar apply.scala
      |""".stripMargin

    /* Just running what we built. */
    def applying: String = s"""
      |java -d${arch} -Xmx${javaHeap}G -cp .:dist.jar apply
      |""".stripMargin

    /* Instance status-tagging. */
    def tag(state: InstanceStatus): String = s"""
      |echo
      |echo " -- ${state} -- "
      |echo
      |aws ec2 create-tags --resources $$ec2id  --tag Key=${api.statusAWSTag},Value=${state} > /dev/null
      |""".stripMargin

    // checks exit code of the previous step
    def tagStep(state: String): String = """
      |tagStep $? $state$
      |""".stripMargin.replaceAll("$state$", state)

    def fixLineEndings(s: String): String = s.replaceAll("\\r\\n", "\n").replaceAll("\\r", "\n")

    /* Combining all parts to one script. */
    def userScript[B <: AnyBundle, E >: ami.type <: AnyEnvironment](bundle: B)(implicit comp: E => Compatible[E, B]): String =
      
      fixLineEndings(
        "#!/bin/sh \n"       + initSetting +
        tagStep(s"${api.preparing}") + preparing +
        tagStep(s"${api.building}")  + building(bundle, comp(this).metadata) +
        tagStep(s"${api.applying}")  + applying +
        tagStep(s"${api.success}")
      )
  }

  object AmazonLinuxAMI {

    implicit def getScriptOps[B0 <: AnyBundle, A0 <: AmazonLinuxAMI]
      (a: A0)(implicit v: A0 => Compatible[A0, B0]): ScriptOps[B0,A0] = 
      ScriptOps(a)(v(a))

    implicit def getScriptOpsAlt[B0 <: AnyBundle, A0 <: AmazonLinuxAMI]
      (a: A0)(implicit c: Compatible[A0, B0]): ScriptOps[B0,A0] = 
      ScriptOps(a)

    case class ScriptOps[B <: AnyBundle, A <: AmazonLinuxAMI](val a: A)(implicit val comp: Compatible[A,B]) {

      final def userScript(bundle: B): String = 
        a.fixLineEndings(
          "#!/bin/sh \n"                      + 
          a.initSetting                       +
          a.tagStep(s"${api.preparing}")      + 
          a.preparing                         +
          a.tagStep(s"${api.building}")       + 
          a.building(bundle, comp.metadata)   +
          a.tagStep(s"${api.applying}")       + 
          a.applying                          +
          a.tagStep(s"${api.success}")
        )
    }
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

    val arch = x64
    val workingDir = "/media/ephemeral0/applicator"
  }

}

// TODO: make it dependent on instance type and choose PV or HVM
// See http://aws.amazon.com/amazon-linux-ami/instance-type-matrix/