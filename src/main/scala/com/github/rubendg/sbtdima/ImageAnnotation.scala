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

import java.net.URL

final case class ImageAnnotation(created: Option[Instant] = None,
                                 title: Option[String] = None,
                                 description: Option[String] = None,
                                 documentation: Option[URL] = None,
                                 url: Option[URL] = None,
                                 source: Option[URL] = None,
                                 revision: Option[String] = None,
                                 vendor: Option[String] = None,
                                 version: Option[String] = None,
                                 authors: Option[String] = None,
                                 licenses: Option[String] = None,
                                 ref: Option[Ref] = None)

final case class Ref(name: Option[String] = None)

private[sbtdima] object ImageAnnotation {

  /**
    *
    * @param name
    * @param labels
    * @return
    */
  private[sbtdima] def ns(name: String, labels: Map[String, String]): Map[String, String] =
    labels.map { case (k, v) => (s"$name.$k", v) }

  /**
    *
    * @param imageAnnotation
    * @return
    */
  private[sbtdima] def toMap(imageAnnotation: ImageAnnotation): Map[String, String] = {

    def formatImageAnnotation(imageAnnotation: ImageAnnotation): Map[String, String] = {
      import imageAnnotation._

      ns(
        "org.opencontainers.image", {
          Seq(
            created.map("created" -> _.toString),
            title.map("title" -> _),
            description.map("description" -> _),
            documentation.map("documentation" -> _.toExternalForm),
            url.map("url" -> _.toExternalForm),
            source.map("source" -> _.toExternalForm),
            revision.map("revision" -> _),
            vendor.map("vendor" -> _),
            version.map("version" -> _),
            authors.map("authors" -> _),
            licenses.map("licenses" -> _),
            ref.flatMap(_.name.map("ref.name" -> _))
          ).flatMap(_.toList)
        }.toMap
      )
    }

    formatImageAnnotation(imageAnnotation)
  }
}
