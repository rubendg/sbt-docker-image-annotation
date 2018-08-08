import sbt.Keys._

import java.time.Instant

version := "0.1.0"

dockerImageAnnotation in Docker := (dockerImageAnnotation in Docker).value
  .copy(created = Some(Instant.parse("2017-07-23T12:16:28.634Z")))

enablePlugins(JavaAppPackaging)

TaskKey[Unit]("checkIfLabelsAreApplied") := {
  val process = scala.sys.process.Process(
    "docker",
    Seq(
      "inspect",
      "--format={{range $k, $v := .Config.Labels}}{{$k}}={{$v}} {{end}}",
      s"${name.value}:${version.value}"
    )
  )

  val expectedOutput = Seq(
    "org.opencontainers.image.created=2017-07-23T12:16:28.634Z",
    "org.opencontainers.image.description=defaults",
    "org.opencontainers.image.title=defaults",
    "org.opencontainers.image.vendor=default",
    "org.opencontainers.image.version=0.1.0"
  ).mkString(" ")

  val out = process.!!.trim().split("\n").head

  assert(out == expectedOutput, s"$out not equal to $expectedOutput")

  ()
}
