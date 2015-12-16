## Statika

[![](https://travis-ci.org/ohnosequences/statika.svg?branch=master)](https://travis-ci.org/ohnosequences/statika)
[![](https://img.shields.io/codacy/3da071706ad94290ba771a525ae087e7.svg)](https://www.codacy.com/app/era7/statika)
[![](https://img.shields.io/github/release/ohnosequences/statika.svg)](https://github.com/ohnosequences/statika/releases/latest)
[![](https://img.shields.io/badge/license-AGPLv3-blue.svg)](https://tldrlegal.com/license/gnu-affero-general-public-license-v3-%28agpl-3.0%29)
[![](https://img.shields.io/badge/contact-gitter_chat-dd1054.svg)](https://gitter.im/ohnosequences/statika)

> managing dependencies on the type-level

Statika is a set of Scala libraries, which allows you to declare dependencies between components of any modular system and deploy them using Amazon Web Services.

This allows one to create configurations of modules, for example, for applying it to a set of Amazon EC2 computation instances, being statically insured that it won’t fail because of dependencies resolution issues.

The main component of _Statika_ is a _bundle_. It is a thin wrapper for a tool, library, resource or any other component of your system.

  + it can depend on other bundles
  + it can do something in runtime, e.g. install a tool, that it represents, or prepare resources.

Then you can deploy a bundle to an Amazon EC2 instance and it will set up the needed environment and install all dependencies.
