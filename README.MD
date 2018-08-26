# sbt-docker-image-annotation

[![Build Status](https://travis-ci.org/rubendg/sbt-docker-image-annotation.svg?branch=master)](https://travis-ci.org/rubendg/sbt-docker-image-annotation)
[![Download](https://api.bintray.com/packages/rubendg/sbt-plugins/sbt-docker-image-annotation/images/download.svg) ](https://bintray.com/rubendg/sbt-plugins/sbt-docker-image-annotation/_latestVersion)

sbt-docker-image-annotation is a [sbt](http://www.scala-sbt.org/) plugin for automatically applying [OCI image annotations](https://github.com/opencontainers/image-spec/blob/master/annotations.md#pre-defined-annotation-keys)
to Docker images created by the [sbt-native-packager](http://www.scala-sbt.org/sbt-native-packager/) plugin.

## Usage Instructions

To enable the plugin add the following lines to your `project/plugins.sbt` file:

```scala
resolvers += Resolver.bintrayIvyRepo("rubendg", "sbt-plugins")

addSbtPlugin("com.github.rubendg.sbtdima" % "sbt-docker-image-annotation" % "0.1.0")
```

The plugin currently supports sbt versions `>= 0.13.16` and `1.x`, and is tested against `sbt-native-packager`
`1.2.2` and `1.3.3`. The plugin **does not** automatically include a dependency on the `sbt-native-packager` 
you'll have to do that yourself:

```scala
addSbtPlugin("com.typesafe.sbt" %% "sbt-native-packager" % "1.3.3")
```

To see the result of activating the plugin run:

```bash
# docker image
sbt docker:publishLocal

# and inspect the image's labels
docker inspect --format='{{.Config.Labels}}' "<IMAGE ID HERE>"
```

this will by default apply a sensible mapping from already existing sbt keys to OCI image annotations.

## SBT to OCI Image Annotation Mapping

This table describes the default mapping from SBT keys (or otherwise) to the `org.opencontainers.image` annotation namespace.

| source                                                         | org.opencontainers.image           |
|----------------------------------------------------------------|------------------------------------|
| now                                                            | created                            |
| sbt.Keys.projectInfo.organizationHomepage                      | url                                |
| sbt.Keys.scmInfo.browserUrl or git remote url                  | source                             |
| sbt.Keys.organizationName                                      | vendor                             |
| sbt.Keys.name                                                  | title                              |
| sbt.Keys.description                                           | description                        |
| sbt.Keys.version                                               | version                            |
| git commit head                                                | revision                           |
| sbt.Keys.apiUrl                                                | documentation                      |
| sbt.Keys.developers                                            | authors, in "name" <email> format  |
| sbt.Keys.licenses                                              | licenses, if multiple are specified will use the SPDX conjunctive expression form |  
| -                                                              | ref.name                           |

Note that when a mapping is not satisfactory it can always be modified through the `dockerImageAnnotation` setting, e.g. like this:

```scala
dockerImageAnnotation in Docker := (dockerImageAnnotation in Docker).value.copy(
  description = Some("More extensive description here")
)
```

## Development

To run the unit tests:

```
sbt test
```

and to run the integration tests:

```
sbt ^scripted
```

## Contribution Policy

Contributions via GitHub pull requests are gladly accepted from their original author.
Along with any pull requests, please state that the contribution is your original work 
and that you license the work to the project under the project's open source license. 
Whether or not you state this explicitly, by submitting any copyrighted material via 
pull request, email, or other means you agree to license the material under the 
project's open source license and warrant that you have the legal authority to do so.

# License 

This code is open source software licensed under the [MIT License](https://opensource.org/licenses/MIT).
