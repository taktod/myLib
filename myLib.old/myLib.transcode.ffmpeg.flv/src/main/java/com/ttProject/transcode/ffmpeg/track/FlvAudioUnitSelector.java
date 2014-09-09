/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.transcode.ffmpeg.track;

import com.ttProject.media.Unit;
import com.ttProject.media.flv.tag.AudioTag;

/**
 * 
 * @author taktod
 */
public class FlvAudioUnitSelector implements IUnitSelector {
	@Override
	public boolean check(Unit unit) {
		return unit instanceof AudioTag;
	}
	@Override
	public void close() {
		
	}
}
