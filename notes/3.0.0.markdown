* Updated project to Scala 2.11/2.12 (cross-published)
* #24: Split the project again:
  - removed aws-related code and dependency
  - moved it back to the aws-statika repo

Note this is a major release, because it contains an important breaking change. Now this project contains only the core statika code, which is used for writing bundles. For the AWS-related code refer to the [ohnosequences/aws-statika](https://github.com/ohnosequences/aws-statika) project.
