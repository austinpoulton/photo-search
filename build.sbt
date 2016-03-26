name := "photo-search"

version := "1.0"

scalaVersion := "2.11.7"

resolvers ++= Seq(
  "Scala libs" at "http://scala-tools.org/repo-releases",
  "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"
)

libraryDependencies ++= Dependencies.projectDeps

unmanagedBase := baseDirectory.value / "custom_lib"

fork in run := true