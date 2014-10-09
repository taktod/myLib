/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.opus;

import org.apache.log4j.Logger;

import com.ttProject.frame.AudioSelector;
import com.ttProject.frame.opus.type.CommentFrame;
import com.ttProject.frame.opus.type.Frame;
import com.ttProject.frame.opus.type.HeaderFrame;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.IUnit;
import com.ttProject.util.BufferUtil;

/**
 * selector for opus frame.
 * expect to have the data in order.
 * headerFrame -> data.
 * @author taktod
 */
public class OpusFrameSelector extends AudioSelector {
	/** logger */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(OpusFrameSelector.class);
	/** header */
	private HeaderFrame headerFrame = null;
	/**
	 * {@inheritDoc}
	 */
	@Override
	public IUnit select(IReadChannel channel) throws Exception {
		if(channel.position() == channel.size()) {
			return null;
		}
		OpusFrame frame = null;
		// ここはきた順番ではなく、先頭がOpusTagsもしくはOpusHeadである場合は・・・で場合分けした方がよさそう。
		// 1文字目をみて、OだったらOpusである可能性があるとする(いきなり８文字だと、それ以下のデータ量であることがあるっぽい。)
		byte firstByte = BufferUtil.safeRead(channel, 1).get();
		if(firstByte == 'O' && channel.size() > 8) {
			// expect to have Opus.... , read 7 byte more.
			String sigString = new String(BufferUtil.safeRead(channel, 7).array());
			if(sigString.equals("pusHead")) {
				// headerFrame
				frame = new HeaderFrame();
				headerFrame = (HeaderFrame)frame;
			}
			else if(sigString.equals("pusTags")) {
				// commentFrame
				frame = new CommentFrame();
			}
			else {
				throw new Exception("unknwon frame.:O" + sigString);
			}
		}
		else {
			// ordinaly frame
			frame = new Frame(firstByte);
		}
		if(!(frame instanceof HeaderFrame)) {
			frame.setHeaderFrame(headerFrame);
		}
		frame.minimumLoad(channel);
		return frame;
	}
}
