# How to apply a bundle to an EC2 instance


## Intro

Purpose of statika bundle is to do some actual work, while have all needed dependencies under compile-time control. So bundles are supposed to be _applied_ to some machine, i.e. do their work on it, be installed with all their dependencies in other words.


## Requirements

This tutorial describes some possible ways of applying a bundle to an EC2 instance. Firstly, to apply a bundle, you need several things:
* Bundle and distribution artifacts information
* Bundle and distribution object names (how to you refer to them in code)
* Credentials for launching an instance


## Application from the command line

Just use `statika` command line tool, which has a special command for applying bundles. See [it's documentation](https://github.com/ohnosequences/statika-cli/README.md#applying-a-bundle) for instructions.


## Application from the code

### Credentials

Credentials are those ones which may be needed to resolve bundle dependency on the instance. There are several ways to provide them:

* `NoCredentials` — if no credentials is needed
* `RoleCredentials` — use temporary instance role credentials
* `InBucket(url: String)` — file with credentials, which lies in an S3 bucket
* `Explicit(accessKey: String, secretKey: String)` — just explicit keys

If the bundle is public and all of the dependencies are public, you can use `NoCredentials`, and if the bundle is private, the recommended option is `RoleCredentials`, which assumes, that you have a special role with the access to the private buckets (where the bundles are published), and you set the corresponding to that role instance profile ARN in you [sbt-statika-plugin](docs/how-to-set-up-statika.md) or in your distribution.


### Launching an instance

You can use `aws-scala-tools` or even `aws-java-sdk`:

```scala
import ohnosequences.awstools.ec2._

val ec2 = EC2.create(new java.io.File("/path/to/my/AwsCredentials.properties"))

val specs = InstanceSpecs(
    instanceType = InstanceType.InstanceType("c1.medium")
  , amiId = FooDistribution.ami.id
  , keyName = "statika-launcher"
  , deviceMapping = Map()
  , userData = FooDistribution.userScript(BarBundle)
  , instanceProfileARN = Some("...")
  )

val instances = ec2.runInstances(1, specs)
```

The `keyName` parameter should be a name of the ssh keys, so that you can connect to the instance later and check what's going on with you bundle.

So, you can use this code snippet, if you want, but again, you need credentials and the user script. Sbt dependency for using `aws-scala-tools` looks like this:

```scala
"ohnosequences" %% "aws-scala-tools" % "0.2.3"
```

(`0.2.3` is not the latest version, but code above uses it, so you can update it, if you will use the latest version)

You can also use `statika-cli` from code, as it provides some convenient functions to launch instances and tracking status of the application process:

```scala
import ohnosequences.statika.cli.StatikaEC2._

val ec2 = EC2.create(new java.io.File("/path/to/my/AwsCredentials.properties"))

val specs = InstanceSpecs(
   // the same as before ...
  )

// this outputs application status updates:
val instances = ec2.applyAndWait("BarBundle", specs)
```
