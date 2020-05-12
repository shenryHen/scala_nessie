scalaVersion := "2.13.1"
name := "scala-nessie"
organization := "cof"
version := "1.0"
trapExit := false

//libraryDependencies +=  "org.scalaj" %% "scalaj-http" % "2.4.2"
libraryDependencies += "com.typesafe.akka" %% "akka-http"   % "10.1.11" 
libraryDependencies += "com.typesafe.akka" %% "akka-stream" % "2.5.26" // or whatever the latest version is
libraryDependencies += "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.11"
libraryDependencies += "com.typesafe.play" %% "play-json" % "2.8.1"	