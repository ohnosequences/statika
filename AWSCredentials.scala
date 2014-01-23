/* 
## AWS Credentials 

Type for credentials with some predefined convenient constructors.
*/

package ohnosequences.statika.aws

trait AWSCredentials
// no credentials is needed
case object NoCredentials extends AWSCredentials
// use temporary instance role credentials
case object RoleCredentials extends AWSCredentials
// file with credentials lies in an S3 bucket
case class InBucket(url: String) extends AWSCredentials
// just explicit keys
case class Explicit(accessKey: String, secretKey: String) extends AWSCredentials
