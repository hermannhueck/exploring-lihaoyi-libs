import sbt._
import Versions._

object Dependencies {

  lazy val libraries =
    Seq(
      "org.scalameta" %% "munit"     % munitVersion, // % Test,
      "com.lihaoyi"   %% "pprint"    % pprintVersion,
      "com.lihaoyi"   %% "os-lib"    % oslibVersion,
      "com.lihaoyi"   %% "upickle"   % upickleVersion,
      "com.lihaoyi"   %% "requests"  % requestsVersion,
      "org.typelevel" %% "cats-core" % catsVersion
    )

  lazy val compilerPlugins =
    Seq(
      compilerPlugin("org.typelevel" % "kind-projector"     % kindProjectorVersion cross CrossVersion.full),
      compilerPlugin("com.olegpy"   %% "better-monadic-for" % betterMonadicForVersion)
    )

  lazy val dependencies =
    if (scalaVersion == scala3Version)
      libraries
    else
      libraries ++ compilerPlugins
}
