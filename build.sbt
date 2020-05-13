import Dependencies._

ThisBuild / scalaVersion     := "2.12.8"
ThisBuild / version          := "1.0.0"
ThisBuild / organization     := "nessie"
ThisBuild / organizationName := "Capital One"

lazy val root = (project in file("."))
  .settings(
    name := "scala_nessie",
  	trapExit := false,
    libraryDependencies += "com.typesafe.akka" %% "akka-http"   % "10.1.11",
  	libraryDependencies += "com.typesafe.akka" %% "akka-stream" % "2.5.26",
  	libraryDependencies += "com.typesafe.play" %% "play-json" % "2.8.1"
  )

// Uncomment the following for publishing to Sonatype.
// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for more detail.

ThisBuild / description := "Hackathon Mentor Nessie project."
ThisBuild / licenses    := List("Apache 2" -> new URL("http://www.apache.org/licenses/LICENSE-2.0.txt"))
ThisBuild / homepage    := Some(url("https://github.com/shenryHen/scala_nessie"))
ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/shenryhen/scala_nessie"),
    "scm:git@github.com:shenryHen/scala_nessie.git"
  )
)
// ThisBuild / developers := List(
//   Developer(
//     id    = "shenryHen",
//     name  = "Hank Shen",
//     email = "henry.shen@capitalone",
//     url   = "https://github.com/shenryHen"
//   )
// )
// ThisBuild / pomIncludeRepository := { _ => false }
// ThisBuild / publishTo := {
//   val nexus = "https://oss.sonatype.org/"
//   if (isSnapshot.value) Some("snapshots" at nexus + "content/repositories/snapshots")
//   else Some("releases" at nexus + "service/local/staging/deploy/maven2")
// }
// ThisBuild / publishMavenStyle := true
assemblyMergeStrategy in assembly := {
 case PathList("META-INF", xs @ _*) => MergeStrategy.discard
 case x => MergeStrategy.first
}
