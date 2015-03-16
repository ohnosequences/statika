package ohnosequences.statika

/*
## Environments concept

The basic idea behind a environment is to group a set of bundles that are published for the 
runtime environment configuration, together with all the underlying infrastructure needed by 
statika to work with it.

This "background stuff" (things like resolvers, artifact - bundle mappings, etc) is essential for
the correct functioning of statika; the goal here is to abstract over it and bring it to the
foreground, giving ways of providing it through code.

Here is the abstract part of environment, while sbt-specific settings are defined at [ohnosequences
/sbt-statika](https://github.com/ohnosequences/sbt-statika) and the AWS-specific part is added in
[ohnosequences/aws-statika](https://github.com/ohnosequences/aws-statika) package.

Summing the following code up, a environment consists of
- a set of _members_ (bundles) that are known to work with this environment;
- _resources management_ interface (_in developement_);
- methods for correct bundles _installation_ with respect to their dependencies;
*/

object environments {

  import bundles._
  import installations._

  import ohnosequences.cosas._, typeSets._
  import ohnosequences.cosas.ops.typeSets._

}
