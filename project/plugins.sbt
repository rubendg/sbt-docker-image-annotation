addSbtPlugin("de.heikoseeberger" %% "sbt-header" % "3.0.2")
addSbtPlugin("com.typesafe.sbt" %% "sbt-git" % "0.9.3")
addSbtPlugin("org.foundweekends" %% "sbt-bintray" % "0.5.1")
addSbtPlugin("com.timushev.sbt" %% "sbt-updates" % "0.3.1")
addSbtPlugin("com.lucidchart" %% "sbt-scalafmt" % "1.15")

libraryDependencies += "org.scala-sbt" %% "scripted-plugin" % sbtVersion.value
