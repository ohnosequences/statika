# How to write a distribution


## Distributions concept

The basic idea behind a statika distribution is _to bundle other bundles_, not as dependencies, but as members of some environment, i.e. to group a set of bundles that are supposed to work fine (be deployable/installable) in this environment.

By environment we mean anything that is essential for deploying bundles on a particular platform (AWS here).

There is scala-code part of distribution defining it's behavior and an sbt-specific part defining the sbt-settings common for all the bundles: [ohnosequences/sbt-statika](https://github.com/ohnosequences/sbt-statika).

An abstract distribution is just a normal bundle, which has additionally a set of _members_ and methods for installing them _with their dependencies_.

There is also a notion of AWS-distribution, which extends abstract with AWS-specific settings:

* _AMI_ which will be used for bundles application;
* _metadata_ necessary for deploying, such as sbt resolvers or the url of the published artifact.


## Amazon Machine Image (AMI)

The AMI abstraction in statika represents the part of infrastructure dependent on a particular Amazon Machine image which you will use for launching instances (to apply bundles on them). The main thing which such AMI class should provide is the user script.

User script is a shell script which will be the first thing ran on a new instance. So it should set the minimal necessary environment for applying a bundle. To apply a bundle, this script creates a minimal sbt project dependent on the bundle and the corresponding distribution and runs the installation process.

There is an [AMI library](https://github.com/ohnosequences/amazon-linux-ami), which represents the current version of the official Amazon Linux AMI (EU-Ireland, x64).


## Simple distribution

### Code part

Once you have a prepared AMI (or chose the existing), writing a distribution is really easy. In the end it's just  a set of bundles. So code of a simple distribution consisting of `git` and `foo` and some other bundles will look like this:

```scala
package ohnosequences.statika.distributions

import ohnosequences.statika._

case object SimpleDistribution extends AWSDistribution(
    metadata = generated.metadata.SimpleDistribution
  , ami = ami.AMI44939930
  , members = Git :~: Foo :~: Tophat :~: Bowtie
  )
```

The important part here is `members = ...` — it's just a _set_ of bundles, which are declared to be members of this distribution. Of course, the distribution then _needs to have the corresponding artifacts in sbt-dependencies_.

Another important thing is the metadata. In the normal scenario, you should use automatically generated sbt-metadata. Sbt-statika plugin, which should be used by all bundles (and distributions) provides a metadata object with the name which is just a normalized sbt-project name and is stored in the `metadataObject` sbt key — so you can look it up or change it. For example, if your sbt-project is called `foo-bar.buZz`, then you can access this generated metadata by `generated.metadata.FooBarBuZz` name.

### Sbt part

In the `build.sbt` of your distribution project you should add basically one line:

```
distributionSettings

```

it will 
* add a dependency on aws-statika of the current version (can be changed using `awsStatikaVersion` key);
* generate metadata object (which name is stored in `metadataObject` key);
* add to publishing an artifact, which includes all dependencies (with `fat` classifier).

And of course, as was already mentioned, you should add necessary members artifact dependencies to `libraryDependencies`.


### Testing

The final part of preparing a distribution would be testing, as we declare that the members work with this distribution, i.e. environment setting. For that see the "How to apply a bundle" tutorial, specifically, the section about doing it in code.


### Example

There is a demo distribution in [ohnosequences/statika-distributions](https://github.com/ohnosequences/statika-distributions).
