name := "aggregator"

version := "0.1"

scalaVersion := "2.12.8"

lazy val akkaVersion = "2.5.21"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion,
  "com.typesafe.akka" %% "akka-http" % "10.1.7",
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.7",
  "net.virtual-void" %%  "json-lenses" % "0.6.2",
  "org.scalatest" %% "scalatest" % "3.0.5" % "test",
  "org.mockito" % "mockito-all" % "1.8.4" % Test,
  "org.mock-server" % "mockserver-netty" % "5.5.1" % Test
)
