# How to write a bundle

This is a short tutorial for those, who want to write new Statika bundles.


### 1. Generating new bundle project

To create the sbt-project scafforld for a new bundle, you can use the [giter8](https://github.com/n8han/giter8) template:

```
g8 ohnosequences/statika-bundle.g8
```

In the generated project you care mainly about two files:

- `build.sbt` where you can tweak sbt settings and add artifact dependencies
- `src/main/scala/foo.scala` which contains your bundle source code (assuming that your bundle name is `foo`)


### 2. Declaring dependencies

First thing you should probably do, is declare dependencies on other bundles that you want to use in yours during its installation process. Notice that you should care only about _direct_ dependencies, i.e. only those that you _use_, because their own dependencies will be managed automatically.

The bundle dependencies appear both in its sbt project and the Scala code.

In the sbt part you just need to say what are the _artifacts_ of those bundles:

```scala
libraryDependencies ++= Seq (
  "ohnosequencesBundles" %% "bar" % "0.2.5",
  "ohnosequencesBundles" %% "qux" % "0.3.7",
  "ohnosequencesBundles" %% "buh" % "0.1.2"
)
```

In the code part you just list them as arguments of the `Bundle` constructor:

```scala
case object foo extends Bundle(bar, qux, buh)
```

This means that install methods of `bar`, `qux` and `buh` will be launched before `foo`'s.


### 3. Writing `install` method

Now you can go to `src/main/scala/foo.scala` and fill the `install` method. It returns `Results` which is just a list of either positive or negative messages. To explicitly construct such result you can use two methods: `failure("something went wrong")` or `success("ok!")`. You can return several results, concatenating it: `success("did this") ++ success("did that") ++ failure("but in the end failed")`.

So try to provide useful information about failures, consider different cases, why installation process could fail, catch exceptions and return the reason, so that it will be easier to fix it if something goes wrong.

Note that anything, that can be treated as a `ProcessBuilder` from `sys.process`, can be implicitly converted to `Results`.

You can find more information about instructions in the [docs](src/main/scala/ohnosequences/statika/Instructions.scala.md)

See examples of bundles in the [ohnosequences-bundles](https://github.com/ohnosequences-bundles) github org.


# How to test your bundle

> TODO


# How to apply existing bundles

> TODO
