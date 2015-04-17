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

import java.util.Properties
import java.io.BufferedReader
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader
import java.io.OutputStream;

import com.jcraft.jsch.Channel
import com.jcraft.jsch.ChannelSftp
import com.jcraft.jsch.ChannelExec
import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session

import sbt.Logger

object SSH {

  def submitCommand(
    SFTPHOST: String,
    SFTPPORT: Int, 
    SFTPUSER: String, 
    SFTPKEY: String,     
    COMMAND: String, 
    log: Logger)  = {


    var channel: Channel = null
    var session: Session = null

    try {

      val jsch: JSch = new JSch()    
          jsch.addIdentity(SFTPKEY);      

      val config: Properties = new Properties()      
          config.put("StrictHostKeyChecking", "no")
      
      session = jsch.getSession(SFTPUSER, SFTPHOST, SFTPPORT)
      session.setConfig(config)
      session.connect()            

      channel = session.openChannel("exec")

      log.info(s"Submitting command: $COMMAND")
      val channelExec: ChannelExec = channel.asInstanceOf[ChannelExec]
          channelExec.setCommand(COMMAND);

      
      val in: BufferedReader = new BufferedReader(new InputStreamReader(channel.getInputStream()));

      channel.connect();

      var msg: String =null;
      while({msg=in.readLine(); msg != null}){
       log.info(msg);
      }
    
    } catch {
        case ex: Exception => log.error(ex.toString())
    } finally {       

     channel.disconnect();
     session.disconnect();
      
      log.info("Command submitted successful!")
    } 

  }


  def uploadFile(
    SFTPHOST: String,
    SFTPPORT: Int, 
    SFTPUSER: String, 
    SFTPKEY: String, 
    SFTPDSTDIR: String, 
    FILETOTRANSFER: String, 
    log: Logger)  = {

    var fis: FileInputStream = null
    var channel: Channel = null
    var session: Session = null
    
    try {
      
      val jsch: JSch = new JSch()    
          jsch.addIdentity(SFTPKEY);      

      val config: Properties = new Properties()      
          config.put("StrictHostKeyChecking", "no")
      
      session = jsch.getSession(SFTPUSER, SFTPHOST, SFTPPORT)
      session.setConfig(config)
      session.connect()
      
      channel = session.openChannel("sftp")
      channel.connect()
            
      val channelSftp: ChannelSftp = channel.asInstanceOf[ChannelSftp]
          channelSftp.cd(SFTPDSTDIR)
      
      log.info(s"Transfering file $FILETOTRANSFER to $SFTPHOST:$SFTPDSTDIR")
      val f: File = new File(FILETOTRANSFER)
      fis = new FileInputStream(f)
      
      channelSftp.put(fis, f.getName())    
    
    } catch {
        case ex: Exception => log.error(ex.toString())
    } finally {
        if(fis!=null){
          fis.close()
        }

      channel.disconnect();      
      session.disconnect();
      
      log.info("File transfer successful!")
    } 
  }    
}