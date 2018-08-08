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

import org.scalatest.{Matchers, WordSpec}

import scala.util.{Failure, Success, Try}

final class GitSpec extends WordSpec with Matchers {

  "remoteUrl" should {

    "parse the remoteUrl when present" in {
      val git = new GitSupport {
        override protected def headLines(cmd: String): Try[String] = Success("https://www.example.com")
      }
      git.source shouldBe Success(new URL("https://www.example.com"))
    }

    "return none in case parsing fails" in {
      val git = new GitSupport {
        override protected def headLines(cmd: String): Try[String] = Success("garbage")
      }
      git.source.failed.get.getMessage shouldBe "Failed to parse remote url: garbage"
    }

    "return none in case the remote url could not be obtained" in {
      val git = new GitSupport {
        override protected def headLines(cmd: String): Try[String] = Failure[String](new RuntimeException("err"))
      }
      git.source.failed.get.getMessage shouldBe "err"
    }

    "rewrite a git@domain:user/repo.git style remote url to a https based one" in {
      val git = new GitSupport {
        override protected def headLines(cmd: String): Try[String] = Success("git@github.com:john/some-repo.git")
      }
      git.source shouldBe Success(new URL("https://github.com/john/some-repo.git"))
    }
  }

  "headCommit" should {
    "return the commit hash" in {
      val git = new GitSupport {
        override protected def headLines(cmd: String): Try[String] = Success("hash")
      }
      git.revision shouldBe Success("hash")
    }

    "return none when the head commit should not be obtained" in {
      val git = new GitSupport {
        override protected def headLines(cmd: String): Try[String] = Failure[String](new RuntimeException("err"))
      }
      git.revision.failed.get.getMessage shouldBe "err"
    }
  }

}
