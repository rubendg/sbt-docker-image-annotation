/*
 * Copyright (c) 2018 Ruben de Gooijer
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.github.rubendg.sbtdima

import java.time.Instant

import sbt._
import Keys._
import com.typesafe.sbt.packager.docker.{Cmd, DockerPlugin}

import scala.collection.immutable.Stream._
import scala.util.Try

object DockerImageAnnotationPlugin extends AutoPlugin {

  private def tryLog[A](logger: Logger, t: Try[A], level: Level.Value): Try[A] = {
    t.failed.foreach { e =>
      logger.log(level, e.getMessage)
    }
    t
  }

  object autoImport {
    val dockerImageAnnotation: SettingKey[ImageAnnotation] =
      SettingKey[ImageAnnotation]("dockerImageAnnotation", "The OCI image annotations to be attached as Docker labels")

    val dockerImageAnnotationVcsSupport: SettingKey[VcsSupport] =
      SettingKey[VcsSupport](
        "dockerImageAnnotationVcsSupport",
        "Provides a means to extract information from the version control system"
      )
  }

  import autoImport._
  import DockerPlugin.autoImport._

  override def requires: Plugins = DockerPlugin

  override def trigger: PluginTrigger = allRequirements

  private val imageAnnotationAsDockerLabels: ImageAnnotation => Option[String] =
    ImageAnnotation.toMap _ andThen DockerLabel.fromMap

  lazy val baseDockerImageAnnotationSettings: Seq[Setting[_]] = Seq(
    dockerImageAnnotationVcsSupport := new GitSupport,
    dockerImageAnnotation := ImageAnnotation(
      title = Some(name.value),
      created = Some(Instant.now()),
      description = Some(description.value),
      version = Some(version.value),
      url = projectInfo.value.organizationHomepage,
      vendor = Some(organizationName.value),
      source = (
        scmInfo.value.map(_.browseUrl) #::
          tryLog(sLog.value, dockerImageAnnotationVcsSupport.value.source, Level.Warn).toOption #:: Stream.empty
      ).toSeq.pickFirst,
      revision = tryLog(sLog.value, dockerImageAnnotationVcsSupport.value.revision, Level.Warn).toOption,
      documentation = apiURL.value,
      authors =
        if (developers.value.isEmpty) None
        else Some(developers.value.map(dev => s"${dev.name} <${dev.email}>").mkString(", ")),
      licenses = if (licenses.value.isEmpty) None else Some(licenses.value.map(_._1).mkString(" AND "))
    ),
    dockerCommands ++= imageAnnotationAsDockerLabels(dockerImageAnnotation.value)
      .map(annotations => Seq(Cmd("LABEL", annotations)))
      .getOrElse(Seq.empty)
  )

  override lazy val projectSettings: Seq[Def.Setting[_]] = inConfig(Docker)(baseDockerImageAnnotationSettings)
}
