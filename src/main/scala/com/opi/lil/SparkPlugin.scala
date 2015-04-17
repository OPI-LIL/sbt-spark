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
import complete.DefaultParsers._

object SparkPlugin extends AutoPlugin {

  object autoImport {
    
    // defining task
    val deploy = taskKey[Unit]("Deploy to remote machine")    

    // define settings keys  
    val user = settingKey[String]("User of the Apache Spark Master")
    val host = settingKey[String]("Host of the Apache Spark Master")    
    val port = settingKey[Int]("SSH port")    
    val key = settingKey[String]("Path to prive hey for passwordless login")
    val destFolder = settingKey[String]("Deploy destination folder")
    val defaultClass = settingKey[String]("Default class to be submitted to Apache Spark")  
  }
  
  import autoImport._  
  import StateHelper._
  import SSH._
  
  override def trigger = allRequirements 
  override lazy val projectSettings = Seq(
    defaultClass := "MainApp",
    port := 22, 
    deploy := deployImpl.value,
    commands ++= Seq(submit)
  )
        
  lazy val submit = Command.args("submit", "<className>") { (state, args) =>
    doCommand(state, ifEmpty(args, getSettingValue(defaultClass, state)))    
  }

  lazy val deployImpl: Initialize[Task[Unit]] =
    Def.task {      
      (Keys.`package` in Compile).value   // depends on package task
      
      val log = streams.value.log            
      val jar = new Jar(name.value, version.value, scalaVersion.value)                                    

      SSH.uploadFile(host.value, port.value, user.value, key.value, destFolder.value, jar.filePath, log)
  }

  
  def doCommand(state: State, className: String) : State = {
      val log = state.log
      val extracted = Project.extract(state)
      val (afterDeploy, result) = extracted.runTask(deploy in Compile, state)
      
      val hostVal = getSettingValue(host, state)
      val portVal = getSettingValue(port, state)
      val userVal = getSettingValue(user, state)
      val keyVal = getSettingValue(key, state)
      val destFolderVal = getSettingValue(destFolder, state)
      val className = getSettingValue(defaultClass, state)
      
      val jar = new Jar(
        getSettingValue(name, state),
        getSettingValue(version, state),
        getSettingValue(scalaVersion, state))    

      val command = s"spark-submit --class $className $destFolderVal/${jar.fileName}"

      log.info(command)

      SSH.submitCommand(hostVal, portVal, userVal, keyVal, command, log)
      
      afterDeploy                
  }
}