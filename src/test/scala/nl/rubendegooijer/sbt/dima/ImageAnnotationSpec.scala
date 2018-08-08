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

package nl.rubendegooijer.sbt.dima

import java.net.URL
import java.time.Instant

import org.scalatest.{Matchers, WordSpec}

final class ImageAnnotationSpec extends WordSpec with Matchers {

  "ImageAnnotation" when {

    import ImageAnnotation._

    "marshalled as docker labels" should {

      "conform to the schema for the minimal case" in {
        toMap(ImageAnnotation()) shouldBe Map()
      }

      "conform to the schema for the maximal case" in {

        val fullBlownExample = ImageAnnotation(
          created = Some(Instant.parse("2007-12-03T10:15:30.00Z")),
          title = Some("example"),
          description = Some("some example description"),
          documentation = Some(new URL("https://www.example.com/docs")),
          url = Some(new URL("https://example.com")),
          source = Some(new URL("https://repo.example.com")),
          revision = Some("some sha"),
          vendor = Some("example corp"),
          version = Some("1.0"),
          authors = Some("John Doe <john@do.com>"),
          licenses = Some("LGPL-2.1 AND MIT")
        )

        toMap(fullBlownExample) shouldBe Map(
          "org.opencontainers.image.created" -> "2007-12-03T10:15:30Z",
          "org.opencontainers.image.title" -> "example",
          "org.opencontainers.image.description" -> "some example description",
          "org.opencontainers.image.documentation" -> "https://www.example.com/docs",
          "org.opencontainers.image.url" -> "https://example.com",
          "org.opencontainers.image.source" -> "https://repo.example.com",
          "org.opencontainers.image.revision" -> "some sha",
          "org.opencontainers.image.vendor" -> "example corp",
          "org.opencontainers.image.version" -> "1.0",
          "org.opencontainers.image.authors" -> "John Doe <john@do.com>",
          "org.opencontainers.image.licenses" -> "LGPL-2.1 AND MIT"
        )
      }

    }

  }
}
