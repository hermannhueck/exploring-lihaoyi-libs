val projectName        = "exploring-lihaoyi-libs"
val projectDescription = "Exploring Li Haoyi's libraries"

// turbo mode with ClassLoader layering
ThisBuild / turbo                  := true // default: false
// build definition source watch
Global / onChangedBuildSource      := ReloadOnSourceChanges
// include resolvers from the metabuild
ThisBuild / includePluginResolvers := true // default: false

lazy val commonSettings =
  Seq(
    version                           := Versions.projectVersion,
    scalaVersion                      := Versions.scalaVersion,
    scalacOptions ++= ScalacOptions.scalacOptions,
    Compile / console / scalacOptions := ScalacOptions.consoleScalacOptions,
    Test / console / scalacOptions    := ScalacOptions.consoleScalacOptions,
    libraryDependencies ++= Dependencies.dependencies
  )

lazy val root = (project in file("."))
  .aggregate(pprint, oslib, upickle, requests)
  .settings(commonSettings)
  .settings(
    name        := projectName,
    description := projectDescription
  )

lazy val pprint = (project in file("code/pprint"))
  .dependsOn(util)
  .settings(commonSettings)
  .settings(
    name        := "pprint",
    description := s"$projectDescription - Library 'pprint' example code"
  )

lazy val oslib = (project in file("code/oslib"))
  .dependsOn(util)
  .settings(commonSettings)
  .settings(
    name        := "oslib",
    description := s"$projectDescription - Library 'os-lib' example code"
  )

lazy val upickle = (project in file("code/upickle"))
  .dependsOn(util)
  .settings(commonSettings)
  .settings(
    name        := "upickle",
    description := s"$projectDescription - Library 'upickle' example code"
  )

lazy val requests = (project in file("code/requests"))
  .dependsOn(util)
  .settings(commonSettings)
  .settings(
    name        := "requests",
    description := s"$projectDescription - Library 'requests' example code"
  )

lazy val util = (project in file("code/util"))
  .settings(commonSettings)
  .settings(
    name        := "util",
    description := s"$projectDescription - Utilities"
  )
