organization := "edu.washington.cs.knowitall.chunkedextractor"

name := "chunkedextractor"

description := "Wrapper and implementation for extractors of chunked sentences."

crossScalaVersions := Seq("2.10.3")

scalaVersion <<= crossScalaVersions { (vs: Seq[String]) => vs.head }

libraryDependencies ++= Seq(
    "edu.washington.cs.knowitall" %% "openregex-scala" % "1.1.1",
    "edu.washington.cs.knowitall" % "reverb-core" % "1.4.3",
    "edu.washington.cs.knowitall.nlptools" %% "nlptools-conf-breeze" % "2.4.3",
    "edu.washington.cs.knowitall.nlptools" %% "nlptools-core" % "2.4.3",
    "edu.washington.cs.knowitall.nlptools" %% "nlptools-chunk-opennlp" % "2.4.3",
    "edu.washington.cs.knowitall.nlptools" %% "nlptools-stem-morpha" % "2.4.3",
    "junit" % "junit" % "4.11" % "test",
    "org.specs2" % "specs2" % "1.12.3" % "test" cross CrossVersion.binaryMapped {
      case "2.9.3" => "2.9.2"
      case "2.10.2" => "2.10"
      case x => x
    })

scalacOptions ++= Seq("-unchecked", "-deprecation")

licenses := Seq("Academic License" -> url("http://reverb.cs.washington.edu/LICENSE.txt"))

homepage := Some(url("http://github.com/knowitall/chunkedextractor"))

resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

publishMavenStyle := true

publishTo <<= version { (v: String) =>
  val nexus = "https://oss.sonatype.org/"
  if (v.trim.endsWith("SNAPSHOT"))
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

pomExtra := (
  <scm>
    <url>https://github.com/knowitall/chunkedextractor</url>
    <connection>scm:git://github.com/knowitall/chunkedextractor.git</connection>
    <developerConnection>scm:git:git@github.com:knowitall/chunkedextractor.git</developerConnection>
    <tag>HEAD</tag>
  </scm>
  <developers>
   <developer>
      <name>Michael Schmitz</name>
    </developer>
  </developers>)
