/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.nio.channels;

/**
 * interface for file read.
 * file read channel allows to rewind.
 * @author taktod
 */
public interface IFileReadChannel extends IReadChannel {
	/**
	 * access uri information
	 * @return
	 */
	public String getUri();
}
