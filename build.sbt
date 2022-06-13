name := "auction-system"

version := "0.1"

scalaVersion := "2.13.8"

libraryDependencies ++= Seq(
  "com.typesafe.play"      %% "play-json"               % "2.8.2",
  "com.typesafe"           % "config"                   % "1.3.3",
  "org.scalatestplus.play" %% "scalatestplus-play"      % "5.1.0" % Test,
  "org.mockito"            %% "mockito-scala-scalatest" % "1.16.49" % Test
)
