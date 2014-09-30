/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.pipe;

import java.io.File;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.nio.channels.ReadableByteReadChannel;

/**
 * use process with named pipe.
 * @author taktod
 */
public class PipeHandler {
	/** logger */
	private Logger logger = Logger.getLogger(PipeHandler.class);
	/** name for namedpipe */
	private final String name;
	/** path for namedPipe */
	private final String namedPipe;
	/** command */
	private String processCommand;
	/** extra Env vals */
	private Map<String, String> envExtra = null;
	/** process */
	private Process process = null;
	/**
	 * constructor
	 * @param name
	 * @param pid
	 */
	public PipeHandler(String name, String pid) {
		this.name = name;
		String tmpDir = System.getProperty("java.io.tmpdir");
		if(!tmpDir.endsWith("/")) {
			tmpDir += "/";
		}
		this.namedPipe = tmpDir + "myLib.pipe/" + name + "_" + pid;
	}
	/**
	 * command
	 * ${pipe} is replaced with namedpipe file name.
	 * @param command
	 */
	public void setCommand(String command) {
		this.processCommand = command;
	}
	/**
	 * set extra env vals
	 * @param envExtra
	 */
	public void setEnvExtra(Map<String, String> envExtra) {
		this.envExtra = envExtra;
	}
	/**
	 * ref for namedpipe file
	 * @return
	 */
	public File getPipeTarget() {
		return new File(namedPipe);
	}
	/**
	 * ref for name of pipe
	 * @return
	 */
	public String getPipeName() {
		return name;
	}
	/**
	 * execute process
	 */
	public void executeProcess() throws Exception {
		if(processCommand == null) {
			logger.error("process command is not found.");
			throw new Exception("no process command.");
		}
		setupPipe();
		
		StringBuilder command = new StringBuilder();
		command.append(processCommand.replaceAll("\\$\\{pipe\\}", namedPipe));
		ProcessBuilder processBuilder = new ProcessBuilder("/bin/bash", "-c", command.toString());
		if(envExtra != null) {
			Map<String, String> env = processBuilder.environment();
			for(String key : envExtra.keySet()) {
				String envData = env.get(key);
				if(env == null || "".equals(envData)) {
					envData = envExtra.get(key);
				}
				else {
					envData += ":" + envExtra.get(key);
				}
				env.put(key, envData);
			}
		}
		process = processBuilder.start();
	}
	/**
	 * make unix namedpipe.
	 */
	private void setupPipe() throws Exception {
		File f = new File(namedPipe);
		f.getParentFile().mkdirs();
		f.delete();
		StringBuilder command = new StringBuilder();
		command.append("mkfifo " + namedPipe);
		ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", command.toString());
		Process p = builder.start();
		p.waitFor();
	}
	/**
	 * ref for IReadChannel for process inputStream.
	 * @return
	 */
	public IReadChannel getReadChannel() throws Exception {
		if(process == null) {
			throw new Exception("no process.");
		}
		return new ReadableByteReadChannel(Channels.newChannel(process.getInputStream()));
	}
	/**
	 * ref for process inputStream.
	 * @return
	 * @throws Exception
	 */
	public InputStream getInputStream() throws Exception {
		if(process == null) {
			throw new Exception("no process.");
		}
		return process.getInputStream();
	}
	/**
	 * close
	 */
	public void close() {
		// destroy process
		if(process != null) {
			process.destroy();
		}
		// delete namedpipe.
		new File(namedPipe).delete();
	}
}
