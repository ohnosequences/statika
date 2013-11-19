# How to write a bundle


This is a short tutorial for those, who want to write new statika bundles. So let's write a couple of bundles and you'll see, how simple it is.


## Preparation

First let's install statika command line tools:

```
cs ohnosequences/statika-cli
```


## Bundle config

For your convenience we created a giter8 template of a new bundle project. For generating new bundles, i.e. for filling the template, you need a bundle description in `json` format. You can get a config prefilled with default values using this command:

```
statika json foo
```

It will create a file `foo.json`. Now go to [statika-cli documentation](https://github.com/ohnosequences/statika-cli/blob/master/README.md) and there you will find up-to-date description of the configuration format and other detailes.


## Generating new bundle project

After you finished with json bundle description, just run

```
statika generate foo.json
```

It will create `foo` directory with a prepared sbt-project. 


## Declaring dependencies

The command line statika tool helps you to create a project with already declared dependencies, but in the case if you need to change it or you create a bundle manually, here is an explanation of how to manage them.

The bundle dependencies appear in it's sbt project and it's scala code. In the sbt part you just need to say what are the artifacts of the bundle dependencies. In the code part you can say

* `case object Foo extends Bundle() { ... }` for a bundle without dependencies;
* `case object Foo extends Bundle(set(Bar)) { ... }` for a bundle with one dependency on the bundle `Bar`;
* `case object Foo extends Bundle(Bar :+: Qux :+: Buzz) { ... }` for a bundle with several dependencies;

Dependencies here are just sets, so you can use explicit notation: `∅`, `Bar :+: ∅`, `Bar :+: Qux :+: Buzz :+: ∅` correspondingly. 

When declaring dependencies of a bundle, you should think only about the direct dependencies, not the dependencies of dependencies, because they will be managed automatically.


## Writing `install` method

Now you can go to `src/main/scala/Foo.scala` and fill `install` method — describe install procedure of foo in scala code. If you want to call an external command from scala code:

- add `import sys.process._` line
- use either `Seq("command", "with", "parameters")` or just `"command with parameters"`

`install` method returns `InstallResults` which is just a list of either positive or negative messages. To explicitly construct such result you can use two methods: `failure("something went wrong")` or `success("ok!")`. You can return several results, concatenating it: `success("did this") ::: success("did that") ::: failure("but in the end failed")`. 

So try to provide useful information about failures, consider different cases, why installation process could fail, catch exceptions and return the reason, so that it will be easier to fix it later.

Here is how simple `install` method for foo could look (don't worry about this distribution stuff, it will be explained later):

```scala
import sys.process._

def install[D <: AnyDistribution](dist: D): InstallResults =
  "yum install git -y" ->- success(name+" is installed")
```

Here we assumed that foo is some tool which `yum` can install for us, so we just call `yum install foo -y` (`-y` means "answer to all `yum` questions yes") and if it succeeds, return the corresponding message.

Note that anything, that can be treated as a `ProcessBuilder` from `sys.process`, can be implicitly converted to `InstallResults`.


## Examples

All the existing public bundles live at [github.com/statika](https://github.com/statika).


## Conclusion

So far this is all you need to know to write simple bundles. Next step should be testing and for this you need to understand statika distributions concept (see [aws-statika docs](https://github.com/ohnosequences/aws-statika)). Also, take a look at the [InstallMethods module documentation](code/InstallMethods.md).
