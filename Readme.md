## Statika

Using types for immutable package management. Because we want a _static insurance_, i.e. to know everything we need about dependencies in compile time.


### Basic notions

* A **bundle** is a thin wrapper for a tool, library, resource or _any other component_ of your system.
  + it may have dependencies on other bundles;
  + it may do something in runtime, e.g. install a tool, that it represents.
* A **distribution** is a bundle, which can deploy other bundles (it's _members_):
  + it represents some environment, where you're going to use your bundles;
  + being a member of a distribution means to work fine with this environment;
  + distribution takes care of installing member dependencies first, and then the member itself.


### Components of _statika_

The key components of the project are
* [statika](https://github.com/ohnosequences/statika/) — the core library defining the most abstract concepts;
* [aws-statika](https://github.com/ohnosequences/aws-statika/) — an extension of the abstract library with the things related to [Amazon Web Services (AWS)](http://aws.amazon.com/);
* [sbt-statika](https://github.com/ohnosequences/sbt-statika/) — an sbt plugin, which standardizes the project settings for statika bundles and distributions within one Amazon account;

Convenience tools:
* [statika-bundle.g8](https://github.com/ohnosequences/statika-bundle.g8) — giter8 template of a project for a new statika bundle;
* [statika-cli](https://github.com/ohnosequences/statika-cli/) — a command line tool for using this template (because giter8 is not enough), and for _applying_ (i.e. deploying) existing bundles from the command line;

Examples:
* [statika github org](https://github.com/ohnosequences/statika/) contains all our public bundles (mostly bioinformatics tools);
* [statika-distributions](https://github.com/ohnosequences/statika-distributions/) — a repository with example distributions. There is one distribution which contains all existing public bundles.
* [amazon-linux-ami](https://github.com/ohnosequences/amazon-linux-ami/) — implementation of AMI (Amazon Machine Image) abstraction from aws-statika for the official Amazon Linux AMI.


### Documentation

See it in [docs](docs/) folder.

### Contacts

This project is maintained by [@laughedelic](https://github.com/laughedelic). Join the chat-room if you want to ask or discuss something  
[![Gitter](https://badges.gitter.im/Join Chat.svg)](https://gitter.im/ohnosequences/statika?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge)
