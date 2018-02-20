organization := "io.suggest"
name := "sbt-web-brotli"
description := "sbt-web plugin for brotling assets"

sbtPlugin := true

isSnapshot := true

addSbtPlugin("com.typesafe.sbt" % "sbt-web" % "1.4.3")
//addSbtWeb("1.4.2")

//crossSbtVersions := Seq("1.0.1", "0.13.16")
//crossSbtVersions := Seq("0.13.16")
sbtVersion := "0.13.16"

scalaVersion := "2.10.4"

resolvers += (
  "bintray-nitram509-jbrotli" at "http://dl.bintray.com/nitram509/jbrotli"
)

libraryDependencies ++= Seq(
  "org.meteogroup.jbrotli" % "jbrotli" % "0.5.0"
)
