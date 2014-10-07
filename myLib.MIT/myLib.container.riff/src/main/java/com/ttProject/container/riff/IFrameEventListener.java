/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.riff;

import com.ttProject.frame.IFrame;

/**
 * interface for frame load event.
 * @author taktod
 * riff do have one unit to have all frame data.
 */
public interface IFrameEventListener {
	/**
	 * event when find frame on data analyze.
	 * @param frame
	 */
	public void onNewFrame(IFrame frame);
}
