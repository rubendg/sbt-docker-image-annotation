import sbt.Keys._

import java.time.Instant
import com.github.rubendg.sbtdima.Ref

version := "1.0"
name := "Cookie Delivery Service"
organizationName := "The Cookie Company"
organizationHomepage := Some(new URL("http://www.cookiecompany.com"))
description := "A service that allows you to order cookies"
apiURL := Some(new URL("http://www.cookiecompany.com/docs"))
developers := List(Developer(id = "rubendg", name = "Ruben de Gooijer", email = "foo@bar.com", url = new URL("http://www.rubendegooijer.nl")))
licenses += ("MIT", new URL("https://opensource.org/licenses/MIT"))
licenses += ("BSD-2-Clause-Patent", new URL("https://opensource.org/licenses/BSDplusPatent"))

dockerImageAnnotation in Docker := {
  (dockerImageAnnotation in Docker).value
    .copy(
      created = Some(Instant.parse("2017-07-23T12:16:28.634Z")),
      revision = Some("123"),
      ref = Some(Ref(Some("delivery-service")))
    )
}

enablePlugins(JavaAppPackaging)

TaskKey[Unit]("checkIfLabelsAreApplied") := {

  val process = scala.sys.process.Process(
    "docker",
    Seq(
      "inspect",
      "--format={{range $k, $v := .Config.Labels}}{{$k}}={{$v}} {{end}}",
      s"${normalizedName.value}:${version.value}"
    )
  )

  val expectedOutput = Seq(
    "org.opencontainers.image.authors=Ruben de Gooijer <foo@bar.com>",
    "org.opencontainers.image.created=2017-07-23T12:16:28.634Z",
    "org.opencontainers.image.description=A service that allows you to order cookies",
    "org.opencontainers.image.documentation=http://www.cookiecompany.com/docs",
    "org.opencontainers.image.licenses=MIT AND BSD-2-Clause-Patent",
    "org.opencontainers.image.ref.name=delivery-service",
    "org.opencontainers.image.revision=123",
    "org.opencontainers.image.title=Cookie Delivery Service",
    "org.opencontainers.image.url=http://www.cookiecompany.com",
    "org.opencontainers.image.vendor=The Cookie Company",
    "org.opencontainers.image.version=1.0"
  ).mkString(" ")

  val out = process.!!.trim().split("\n").head

  assert(out == expectedOutput, s"$out not equal to $expectedOutput")

  ()
}
