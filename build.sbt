import bintray.Keys._

lazy val commonSettings = Seq(
  version in ThisBuild := "0.1.7",
  organization in ThisBuild := "com.opi.lil",
  scalaVersion in ThisBuild := "2.10.4"
)

lazy val root = (project in file(".")).
  settings(commonSettings ++ bintrayPublishSettings: _*).
  settings(
    sbtPlugin := true,
    name := "sbt-spark",
    description := "Plugin for easy jar deployment and submission to remote linux machine running apache spark",
    licenses += ("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.html")),
    publishMavenStyle := false,
    repository in bintray := "sbt-plugins",
    bintrayOrganization in bintray := None
  )

  (vcsUrl in bintray) := Some("git@github.com:1o0ko/sbt-spark.git")


libraryDependencies += "com.jcraft" % "jsch" % "0.1.52"