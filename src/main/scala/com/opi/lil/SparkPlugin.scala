/**
 * Copyright (c) 2015 NATIONAL INFORMATION PROCESSING INSTITUTE
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.opi.lil.sbtspark

import sbt._
import Keys._
import Def.Initialize

object SublimePlugin extends AutoPlugin {

  object autoImport {
    // defining task
    val deploy = taskKey[Unit]("Deploy to remote machine")
    // val submit = inputKey[Unit]("Submit given class from jar to Apache Spark") 

    // define settings key  
    val remote = settingKey[String]("Address to Apache Spark Master")
    val remoteFolder = settingKey[String]("Deploy destination folder")
    val defaultClass = settingKey[String]("Default class to be submitted to Apache Spark")

  }

  import autoImport._  

  override lazy val projectSettings = Seq(commands += helloCommand)
  
  lazy val helloCommand =
    Command.command("hello") { (state: State) =>
      println("Hi!")
      state
    }

  lazy val deployImpl: Initialize[Task[Unit]] =
    Def.task {      
      (Keys.`package` in Compile).value   // depends on package task
      val jar = new JarData(name.value, version.value, scalaVersion.value)          
      val remoteName = remote.value
      val dstFolder = remoteFolder.value
      val srcFolder = jar.fileFolder()
      val fileName = jar.fileName()
      
      println("$remoteName $dstFolder $srcFolder $fileName")  
   }
}