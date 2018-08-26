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

import java.net.URL

import scala.sys.process.Process
import scala.util.{Failure, Success, Try}

private object GitSupport {

  private def parseRemoteUrl(url: String): Option[URL] = {
    if (url.startsWith("https")) Some(url)
    else {
      val extractDomainAndRepo = "^git@([a-z.]+):(.*)".r
      url match {
        case extractDomainAndRepo(domain, repo) =>
          Some(s"https://$domain/$repo")
        case _ => None
      }
    }
  }.flatMap(url => Try(new URL(url)).toOption)

}

class GitSupport extends VcsSupport {
  import GitSupport._

  protected def headLines(cmd: String): Try[String] =
    Try(Process(cmd).!!).map(_.split("\n").head)

  override def revision: Try[String] = headLines("git rev-parse --verify HEAD")

  override def source: Try[URL] = {
    val remoteUrl = headLines("git config --get remote.origin.url")
    remoteUrl.flatMap(url => {
      parseRemoteUrl(url) match {
        case Some(u) => Success(u)
        case None    => Failure(new IllegalArgumentException(s"Failed to parse remote url: $url"))
      }
    })
  }
}
