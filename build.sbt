name := """secret-hit"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  cache,
  ws,
  evolutions,
  "com.beachape" %% "enumeratum" % "1.4.13",
  "com.beachape" %% "enumeratum-play-json" % "1.4.13"
)

resolvers += Resolver.jcenterRepo
libraryDependencies += "com.github.implicitdef" %% "toolbox" % "0.5.0"
