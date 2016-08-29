import com.trueaccord.scalapb.{ScalaPbPlugin => PB}
import Path.rebase

name := "Proto3-ScalaTcpServer"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies ++=Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.4.9",
  "com.typesafe.akka" %% "akka-stream" % "2.4.9",
  "org.scalafx" %% "scalafx" % "8.0.92-R10")

PB.protobufSettings

PB.runProtoc in PB.protobufConfig := { args =>
  com.github.os72.protocjar.Protoc.runProtoc("-v300" +: args.toArray)
}

scalaSource in PB.protobufConfig <<= (sourceDirectory in Compile)(_ / "generated" )

libraryDependencies ++= Seq(
  // For finding google/protobuf/descriptor.proto
  "com.trueaccord.scalapb" %% "scalapb-runtime" % "0.5.29" % PB.protobufConfig
)