/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.mkv.type;

import com.ttProject.container.mkv.MkvMasterTag;
import com.ttProject.container.mkv.Type;
import com.ttProject.frame.IVideoFrame;
import com.ttProject.unit.extra.EbmlValue;

/**
 * Video
 * @author taktod
 */
public class Video extends MkvMasterTag {
	/**
	 * constructor
	 * @param size
	 */
	public Video(EbmlValue size) {
		super(Type.Video, size);
	}
	/**
	 * constructor
	 */
	public Video() {
		this(new EbmlValue());
	}
	/**
	 * setup the information.
	 * @param frame
	 * @throws Exception
	 */
	public void setup(IVideoFrame frame) throws Exception {
		PixelWidth pixelWidth = new PixelWidth();
		pixelWidth.setValue(frame.getWidth());
		addChild(pixelWidth);
		PixelHeight pixelHeight = new PixelHeight();
		pixelHeight.setValue(frame.getHeight());
		addChild(pixelHeight);
	}
}
