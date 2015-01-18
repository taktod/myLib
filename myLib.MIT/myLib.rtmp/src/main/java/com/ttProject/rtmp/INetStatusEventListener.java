/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.rtmp;

import com.ttProject.container.flv.amf.Amf0Object;

/**
 * INetStatusEventListener
 * receive event for netStatus.
 * @author taktod
 */
public interface INetStatusEventListener {
	public void onStatusEvent(Amf0Object<String, Object> obj);
}
