## Statika

> managing dependencies on the type-level

Statika is a set of Scala libraries, which allows you to declare dependencies between components of any modular system and track their correctness using Scala type system.

This allows one to create configurations of modules, for example, for applying it to a set of Amazon EC2 computation instances, being statically insured, that it won’t fail because of dependencies resolution issues.

The main component of _Statika_ is a _bundle_. It is a thin wrapper for a tool, library, resource or any other component of your system.

  + it can depend on other bundles on the type level;
  + it can do something in runtime, e.g. install a tool, that it represents, or prepare resources.

Then you can deploy a bundle to an Amazon EC2 instance and it will set up the needed environment and install all dependencies.

See the [bundles documentation](docs/src/main/scala/Bundles.scala.md) for more.


### Components of _statika_

The key components of the project are

* [statika](https://github.com/ohnosequences/statika/) — the core library defining the most abstract concepts;
* [aws-statika](https://github.com/ohnosequences/aws-statika/) — an extension of the abstract library with the things related to [Amazon Web Services (AWS)](http://aws.amazon.com/);
* [sbt-statika](https://github.com/ohnosequences/sbt-statika/) — an sbt plugin, which standardizes the project settings for Statika bundles within one Amazon account;


Convenience tools:

* [statika-bundle.g8](https://github.com/ohnosequences/statika-bundle.g8) — giter8 template of a project for a new Statika bundle;
* [statika-cli](https://github.com/ohnosequences/statika-cli/) — a command line tool for using this template (because giter8 is not enough), and for _applying_ (i.e. deploying) existing bundles from the command line;


Example projects:

* [Statika github org](https://github.com/ohnosequences/statika/) contains all our public bundles (mostly bioinformatics tools);
* [amazon-linux-ami](https://github.com/ohnosequences/amazon-linux-ami/) — an implementation of AMI (Amazon Machine Image) abstraction from _aws-statika_ for the official Amazon Linux AMI.


### Documentation

You can find it in the [docs/](docs/) folder. Same for the other parts of the project.

### Contacts

This project is maintained by [@laughedelic](https://github.com/laughedelic). Join the chat-room if you want to ask or discuss something  
[![Gitter](https://badges.gitter.im/Join Chat.svg)](https://gitter.im/ohnosequences/statika?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge)
