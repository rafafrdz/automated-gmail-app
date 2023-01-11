ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.12.10"

lazy val root = (project in file("."))
  .settings(
    name := "automated-gmail-app"
  )

libraryDependencies += "com.github.eikek" %% "emil-common" % "0.10.0-M2"  // the core library

libraryDependencies += "com.github.eikek" %% "emil-javamail" % "0.10.0-M2" // implementation module

// available for 2.12, 2.13, 3.0
libraryDependencies += "co.fs2" %% "fs2-core" % "3.4.0"

// optional I/O library
libraryDependencies += "co.fs2" %% "fs2-io" % "3.4.0"

// optional reactive streams interop
libraryDependencies += "co.fs2" %% "fs2-reactive-streams" % "3.4.0"

// optional scodec interop
libraryDependencies += "co.fs2" %% "fs2-scodec" % "3.4.0"

// https://mvnrepository.com/artifact/org.apache.spark/spark-core
libraryDependencies += "org.apache.spark" %% "spark-core" % "3.1.1"

// https://mvnrepository.com/artifact/org.apache.spark/spark-sql
libraryDependencies += "org.apache.spark" %% "spark-sql" % "3.1.1"

// https://mvnrepository.com/artifact/com.typesafe/config
libraryDependencies += "com.typesafe" % "config" % "1.4.2"

val meta = """META.INF(.)*""".r
assembly / assemblyMergeStrategy := {
  case PathList("javax", "servlet", xs @ _*) => MergeStrategy.first
  case PathList(ps @ _*) if ps.last endsWith ".html" => MergeStrategy.first
  case n if n.contains("services") => MergeStrategy.concat
  case n if n.startsWith("reference.conf") => MergeStrategy.concat
  case n if n.endsWith(".conf") => MergeStrategy.concat
  case meta(_) => MergeStrategy.discard
  case x => MergeStrategy.first
}