/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.pipe;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * namedpipe manager
 * @author taktod
 */
public class PipeManager {
	/** logger */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(PipeManager.class);
	/** processes */
	private final Map<String, PipeHandler> handlers = new HashMap<String, PipeHandler>();
	/** processId */
	private static final String pid;
	/**
	 * static initialize
	 */
	static {
		RuntimeMXBean bean = ManagementFactory.getRuntimeMXBean();
		pid = bean.getName().split("@")[0];
	}
	/**
	 * constructor
	 */
	public PipeManager() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				// with shutdown kill all processes.
				for(PipeHandler handler : handlers.values()) {
					handler.close();
				}
			}
		});
	}
	/**
	 * ref for pipeHandler
	 * in the case of no handler, make one.
	 * @param name
	 * @return
	 */
	public synchronized PipeHandler getPipeHandler(String name) {
		PipeHandler handler = handlers.get(name);
		if(handler == null) {
			handler = new PipeHandler(name, pid);
			handlers.put(name, handler);
		}
		return handler;
	}
}
