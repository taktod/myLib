/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container;

import java.util.List;

import com.ttProject.nio.channels.IReadChannel;

/**
 * base of reader for media file.
 * @author taktod
 */
public interface IReader {
	/**
	 * read
	 * @param channel
	 * @return
	 * @throws Exception
	 */
	public IContainer read(IReadChannel channel) throws Exception;
	/**
	 * ref the left data.
	 * @return
	 * @throws Exception
	 */
	public List<IContainer> getRemainData() throws Exception;
}
