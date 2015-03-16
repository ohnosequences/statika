# How to write a bundle

This is a short tutorial for those, who want to write new Statika bundles.


## Preparation

First let's install Statika command line interface:

```
cs ohnosequences/statika-cli
```

(if you don't have `cs` installed yet, check the instructions [here](https://github.com/n8han/conscript#installation))


## Bundle config

For your convenience we created a giter8 template of a new bundle project. For generating new bundles, i.e. for filling the template, you need a bundle description in `json` format. You can get a config prefilled with default values using this command:

```
statika json foo
```

It will create a file `foo.json`. Now go to [statika-cli documentation](https://github.com/ohnosequences/statika-cli/blob/master/README.md) and there you will find description of the configuration format and other details.


## Generating new bundle project

After you finished with json bundle description, just run

```
statika generate foo.json
```

It will create `foo/` directory with a prepared sbt-project. 


## Declaring dependencies

The command line Statika tool helps you to create a project with already declared dependencies, but in the case if you need to change it or you create a bundle manually, here is an explanation of how to manage them.

The bundle dependencies appear in its sbt project and the Scala code. In the sbt part you just need to say what are the _artifacts_ of those bundles. In the code part you can say

```scala
case object Foo extends Bundle(<dependencies>) { ... }
```

Dependencies here are _type sets_: `∅`, `Bar :~: ∅`, `Bar :~: Qux :~: Buzz :~: ∅`. They work like heterogeneous lists with distinct members. You can find more about type sets in the [ohnosequences/cosas](https://github.com/ohnosequences/cosas) project. But for now you don't need to know much about it.

When declaring dependencies for a bundle, you should think only about the direct dependencies, not the dependencies of dependencies, because they will be managed automatically.


## Writing `install` method

Now you can go to `src/main/scala/Foo.scala` and fill `install` method — describe install procedure of foo in Scala code. If you want to call an external command from Scala code:

- add `import sys.process._` line
- use either `Seq("command", "with", "parameters")` or just `"command with parameters"`

`install` method returns `InstallResults` which is just a list of either positive or negative messages. To explicitly construct such result you can use two methods: `failure("something went wrong")` or `success("ok!")`. You can return several results, concatenating it: `success("did this") ::: success("did that") ::: failure("but in the end failed")`. 

So try to provide useful information about failures, consider different cases, why installation process could fail, catch exceptions and return the reason, so that it will be easier to fix it later.

Here is how simple `install` method for foo could look:

```scala
import sys.process._

def install: InstallResults =
  "yum install git -y" ->- success(s"${name} is installed")
```

Here we assumed that foo is some tool which `yum` can install for us, so we just call `yum install foo -y` (`-y` means "answer to all `yum` questions yes") and if it succeeds, return the corresponding message.

Note that anything, that can be treated as a `ProcessBuilder` from `sys.process`, can be implicitly converted to `InstallResults`.

You can find more information about `InstallResults` in the [docs](src/main/scala/InstallMethods.scala.md)


## Examples

All the existing public bundles live at the [github.com/statika](https://github.com/statika) org.


## Conclusion

So far this is all you need to know to write simple bundles. Next step should be testing and for this you need to understand Statika environments concept (see [aws-statika docs](https://github.com/ohnosequences/aws-statika)). 

See also the [bundles documentation](docs/src/main/scala/Bundles.scala.md) for more information about different bundles flavors.
