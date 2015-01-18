/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.rtmp;

import com.ttProject.rtmp.message.IRtmpMessage;

/**
 * IDataListener
 * receive media data stream for netStream play mode.
 * @author taktod
 */
public interface IDataListener {
	public void receive(IRtmpMessage message);
}
