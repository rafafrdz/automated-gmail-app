


lazy val root = (project in file("."))
  .settings(
    name := "automated-gmail-app",
    scalaVersion := "2.13.10",
    version := "1.0.0",
    scalaJSUseMainModuleInitializer := true,
    mainClass := Some("io.github.rafafrdz.Main")
  ).enablePlugins(ScalaJSPlugin)

/** Back-End Dependencies */
libraryDependencies += "com.github.eikek" %% "emil-common" % "0.10.0-M2" // the core library
libraryDependencies += "com.github.eikek" %% "emil-javamail" % "0.10.0-M2" // implementation module
libraryDependencies += "org.typelevel" %%% "cats-core" % "2.9.0"
libraryDependencies += "org.typelevel" %%% "cats-effect" % "3.4.1"
libraryDependencies += "co.fs2" %% "fs2-core" % "3.4.0"
libraryDependencies += "co.fs2" %% "fs2-io" % "3.4.0"
libraryDependencies += "co.fs2" %% "fs2-reactive-streams" % "3.4.0"
libraryDependencies += "co.fs2" %% "fs2-scodec" % "3.4.0"
libraryDependencies += "org.apache.spark" %% "spark-core" % "3.2.2"
libraryDependencies += "org.apache.spark" %% "spark-sql" % "3.2.2"
libraryDependencies += "com.typesafe" % "config" % "1.4.2"

/** Front-End Dependencies */


/** Compile Settings */
val meta = """META.INF(.)*""".r
assembly / assemblyMergeStrategy := {
  case PathList("javax", "servlet", xs@_*) => MergeStrategy.first
  case PathList(ps@_*) if ps.last endsWith ".html" => MergeStrategy.first
  case n if n.contains("services") => MergeStrategy.concat
  case n if n.startsWith("reference.conf") => MergeStrategy.concat
  case n if n.endsWith(".conf") => MergeStrategy.concat
  case meta(_) => MergeStrategy.discard
  case x => MergeStrategy.first
}