/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under GNU LESSER GENERAL PUBLIC LICENSE Version 3.
 */
package com.ttProject.flazr.client;

import com.flazr.rtmp.client.ClientOptions;

/**
 * extends clientOptions.
 * original clientOptions treat as string for data of objectEncoding.
 * this must be number for wowza media server.
 * @author taktod
 */
public class ClientOptionsEx extends ClientOptions {
	/**
	 * hool the url analyze command.
	 */
	@Override
	public boolean parseCli(String[] args) {
		// do normal parse
		boolean result = super.parseCli(args);
		// change the objectEncoding data from string into number.
		if(getParams() != null) {
			Object encoding = getParams().get("objectEncoding");
			if("3".equals(encoding)) {
				putParam("objectEncoding", 3.0);
			}
			else {
				putParam("objectEncoding", 0.0);
			}
		}
		return result;
	};
}
