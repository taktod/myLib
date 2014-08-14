/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.mjpeg;

import org.apache.log4j.Logger;

import com.ttProject.frame.VideoSelector;
import com.ttProject.frame.mjpeg.type.Frame;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.IUnit;

/**
 * 
 * @author taktod
 */
public class MjpegFrameSelector extends VideoSelector {
	/** ロガー */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(MjpegFrameSelector.class);
	@Override
	public IUnit select(IReadChannel channel) throws Exception {
		if(channel.size() == channel.position()) {
			return null;
		}
		// とりあえずこのデータをそのままframeに保持させれば、frame用のbyteデータはできあがることになる。
		Frame frame = new Frame();
		setup(frame);
		// channelから必要なデータを取り出したい。
//		throw new Exception("データ作成が未実装");
		frame.minimumLoad(channel);
		return frame;
	}
}
