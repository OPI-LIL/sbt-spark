# sbt-spark

Plugin for easy jar deployment and submission to remote linux machine running apache spark

## Install

### What you need

- a linux machine with key-based authorization

Add the following to your sbt `project/plugins.sbt` file:

```scala
resolvers += Resolver.url(
	"opi-lil-sbt-plugins", 
	  url("http://dl.bintray.com/1o0ko/sbt-plugins"))(
	    Resolver.ivyStylePatterns)

addSbtPlugin("com.opi.lil" % "sbt-spark" % "0.1.7")
```

## Usage

Now in your `build.sbt` file you should include the following

```scala
scalaVersion := "2.10.4"

lazy val commonSettings = Seq(
  organization := "com.opi.lil",              // your organization name
  version := "1.0",                           // your project version
  scalaVersion := "2.10.4",                   // your scala version
  user := "user",                             // your username on host
  host := "0.0.0.0",                          // your host adress  
  key := "PATH_TO_YOUR_PRIVATE_KEY",          // path to private key in OpenSSH format
  destFolder := "/home/spark/dev/",           // dest directory of jar file
  defaultClass := "MainApp"                   // class to be submitted to apache spark
)

lazy val root = (project in file(".")).
  settings(commonSettings: _*).
  settings(
  	name := "spark-test",  
    libraryDependencies ++= Seq(
      "org.apache.spark"  % "spark-core_2.10"  % "1.2.0", 
      "org.apache.spark"  % "spark-mllib_2.10" % "1.2.0",
      "org.apache.hadoop" % "hadoop-client" % "2.5.0",
      "com.typesafe" % "config" % "1.0.0")  
  )
```

In order to deoploy project jar file to remote server you just have to type `deploy` in sbt command line

    > deploy

To submit given class to Apache Spark type `submit ClassName`   

    > submit ClassName
  
If the class name is not explicitly stated the class from `defaultClass` from settings will be used. 
