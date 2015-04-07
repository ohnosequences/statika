# How to write a bundle

This is a short tutorial for those, who want to write new Statika bundles.


## Generating new bundle project

After you finished with json bundle description, just run

```
g8 ohnosequences/statika-bundle.g8
```

It will ask you something and create a directory with the prepared sbt-project.


## Declaring dependencies

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



## Application test

To try to apply the bundle to an instance, you can just run

```
sbt test
```

This should launch a small instance and run the install method of your bundle on it. But You should first take a look at the test source code, because there are some important parameters, like credentials and instance type.



## Examples

All the existing public bundles live at the [github.com/statika](https://github.com/statika) org.


## Conclusion

So far this is all you need to know to write simple bundles. Next step should be testing and for this you need to understand Statika environments concept (see [aws-statika docs](https://github.com/ohnosequences/aws-statika)).

See also the [bundles documentation](docs/src/main/scala/Bundles.scala.md) for more information about different bundles flavors.
