ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.1"

lazy val root = (project in file("."))
  .settings(
    name := "TapirShoppingCart"
  )
val tapirVersion = "1.2.10"

libraryDependencies ++= Seq(
  "com.softwaremill.sttp.tapir" %% "tapir-core" % tapirVersion,
  "com.softwaremill.sttp.tapir" %% "tapir-http4s-server-zio" % "1.4.0",
  "com.softwaremill.sttp.tapir" %% "tapir-json-zio" % tapirVersion,
  "com.softwaremill.sttp.tapir" %% "tapir-sttp-client" % tapirVersion,
  "com.softwaremill.sttp.tapir" %% "tapir-openapi-docs" % "1.9.6",
  "com.softwaremill.sttp.apispec" %% "openapi-circe-yaml" % "0.7.3",
  "org.http4s" %% "http4s-blaze-server" % "0.23.14",
  "com.softwaremill.sttp.client3" %% "zio" % "3.8.13",
  "dev.zio" %% "zio-interop-cats" % "2.5.1.0"

)