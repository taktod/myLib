/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.unit;

import com.ttProject.nio.channels.IReadChannel;

/**
 * selector for units
 * minimum loading for units.
 * (ex, selector decide the h264 frame is which nalu, slice? sliceIDR?, however, not load the data.)
 * @author taktod
 */
public interface ISelector {
	/**
	 * select the unit from channel, not load data body.
	 * @param channel
	 * @return
	 * @throws Exception
	 */
	public IUnit select(IReadChannel channel) throws Exception;
}
