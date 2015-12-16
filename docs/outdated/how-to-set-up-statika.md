# How to set up statika


## Intro

This tutorial describes how to set up environment for working with (aws-)statika for a new Amazon account. 


## Amazon account


### S3 buckets for publishing artifacts

The resolver plugin which is normally used for publishing bundle artifacts is supposed to create buckets, if they don't exist. So all you need to do, is to set correctly `bucketSuffix` and/or `publishBucketSuffix` (if you want it to be different) keys in your sbt projects. See [sbt-statika]() documentation for details.


### Accessing private buckets

For resolving private dependencies, you will need to have access to the private S3 buckets. For that you should use an IAM role which gives these permissions, and then use when applying bundles to instances.

You can just go to the AWS Console, IAM service and create a role. The key thing which is needed for that is the right policy. For example, here is the key part of the policy used for ohnosequences statika:

```json
"Statement": [
  {
    "Action": [
      "s3:GetObject",
      "s3:ListBucket",
      "s3:PutObject"
    ],
    "Resource": [
      "arn:aws:s3:::private.releases.statika.ohnosequences.com/*",
      "arn:aws:s3:::private.snapshots.statika.ohnosequences.com/*"
    ],
    "Effect": "Allow"
  }
]
```

Once you created such role, you should get it's _instance profile ARN_ in the summary section and this is what you will actually use.


## Publishing

When you have written a bundle, you want to publish it, because otherwise it cannot be applied. So to publish anything to your buckets, you need AWS credentials: _access key_ and _secret key_. You can get them from you Amazon account web page and then put to a file in the following format:

```
accessKey = 322wasa923...
secretKey = 2342xasd8fDfaa9C...
```

Now, sbt-statika plugs in to your project [_sbt-s3-resolver_](https://github.com/ohnosequences/sbt-s3-resolver), is used to publish your artifacts to the buckets. See this plugin [documentation](https://github.com/ohnosequences/sbt-s3-resolver/blob/master/Readme.md) for the up to date information on that.

Once you've set credentials in sbt, you can publish shapshots (check that the `version` setting in `version.sbt` has the `-SNAPSHOT` suffix) with

```
sbt publish
```

and publish releases with

```
sbt release
```
