import sbt.Keys.{homepage, scmInfo}

import scala.sys.process._

lazy val mvel = project
  .in(file("."))
  .aggregate(
    languageServer,
  )

lazy val commonSettings = Seq(

  version := "1.0",
  resolvers += "dhpcs at bintray" at "https://dl.bintray.com/dhpcs/maven",
  logLevel := Level.Info,
  logBuffered in Test := false,
  scalaVersion := "2.13.1",
  scalacOptions += "-deprecation",
  scalacOptions += "-feature",
  scalacOptions += "-language:implicitConversions",
  scalacOptions += "-language:postfixOps",

  libraryDependencies += "org.scalatest" %% "scalatest" % "3.1.0" % "test"
)

lazy val assemblySettings = Seq(

  assemblyJarName in assembly := name.value + ".jar",
  test in assembly := {},
  assemblyMergeStrategy in assembly := {
    case PathList("META-INF", xs @ _*) => MergeStrategy.discard
    case _                             => MergeStrategy.first
  }
)

def languageServerCommonTask(assemblyFile: String) = {
  val extension = assemblyFile.split("\\.").last
  val removePrevious = Process(Seq("rm", "-f", "./vscode-extension/out/MVELLanguageServer.jar"))
  val mkOutDir = Process(Seq("mkdir", "-p", s"./vscode-extension/out"))
  val copyJar = Process(Seq("cp", assemblyFile, s"./vscode-extension/out/MVELLanguageServer.${extension}"))
  val yarn = Process(Seq("yarn", "compile"), file("./vscode-extension"))
  mkOutDir.#&&(removePrevious).#&&(copyJar).#&&(yarn)
}

def vscodeCommonTask(assemblyFile: String) = {
  val extensionDirectory = file("./vscode-extension").getAbsolutePath
  val vscode = Process(Seq("code", s"--extensionDevelopmentPath=$extensionDirectory"), None)
  languageServerCommonTask(assemblyFile).#&&(vscode)
}

lazy val languageServer = project.
  in(file("languageServer")).
  settings(commonSettings: _*).
  settings(assemblySettings: _*).
  settings(
    vscode := {
      vscodeCommonTask(assembly.value.getAbsolutePath).run
    },

    vscodeprepublish := {
      val assemblyFile: String = assembly.value.getAbsolutePath
      val copyJar = Process(Seq("cp", assemblyFile, s"./vscode-extension/out/MVELLanguageServer.jar"))
      copyJar.run
    },
  ).
  settings(
    name := "languageServer",

    mainClass in Compile := Some("mvel.Program"),
    // https://mvnrepository.com/artifact/com.typesafe.play/play-json
    libraryDependencies += "com.lihaoyi" %% "upickle" % "0.8.0",

    // https://mvnrepository.com/artifact/com.github.keyboardDrummer/modularlanguages
    libraryDependencies += "com.github.keyboardDrummer" %% "modularlanguages" % "0.1.7"
  )

lazy val vscode = taskKey[Unit]("Run VS Code")
lazy val vscodeprepublish = taskKey[Unit]("Build VS Code")
