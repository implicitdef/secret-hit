name := """secret-hit"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  cache,
  ws,
  "org.postgresql" % "postgresql" % "9.4.1209.jre7",
  "com.typesafe.play" %% "play-slick" % "2.0.2",
  "com.typesafe.play" %% "play-slick-evolutions" % "2.0.2",
  "com.typesafe.slick" %% "slick-codegen" % "3.1.0",
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test
)

resolvers += Resolver.jcenterRepo
libraryDependencies += "com.github.implicitdef" %% "toolbox" % "0.5.0"
