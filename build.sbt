organization := "edu.washington.cs.knowitall.common-scala"

name := "chunkedextractor"

version := "1.0.2-SNAPSHOT"

scalaVersion := "2.9.2"

resolvers ++= Seq("oss snapshot" at "http://oss.sonatype.org/content/repositories/snapshots/")

resolvers ++= Seq("oss snapshot 2" at "https://oss.sonatype.org/content/repositories/eduwashingtoncsknowitall-404/")

resolvers ++= Seq("oss snapshot 3" at "https://oss.sonatype.org/content/repositories/eduwashingtoncsknowitall-457/")

libraryDependencies ++= Seq(
    "edu.washington.cs.knowitall" %% "openregex-scala" % "1.0.4",
    "edu.washington.cs.knowitall" % "reverb-core" % "1.4.1",
    "edu.washington.cs.knowitall.nlptools" %% "nlptools-core" % "2.4.0",
    "edu.washington.cs.knowitall.nlptools" %% "nlptools-chunk-opennlp" % "2.4.0",
    "edu.washington.cs.knowitall.nlptools" %% "nlptools-stem-morpha" % "2.4.0",
    "junit" % "junit" % "4.11",
    "org.specs2" %% "specs2" % "1.12.3")

publishMavenStyle := true

publishTo <<= version { (v: String) =>
  val nexus = "https://oss.sonatype.org/"
  if (v.trim.endsWith("SNAPSHOT"))
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}
