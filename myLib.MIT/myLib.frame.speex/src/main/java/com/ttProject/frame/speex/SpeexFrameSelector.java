/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.speex;

import com.ttProject.frame.AudioSelector;
import com.ttProject.frame.speex.type.CommentFrame;
import com.ttProject.frame.speex.type.Frame;
import com.ttProject.frame.speex.type.HeaderFrame;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.IUnit;

/**
 * selector for speex frame.
 * expect to get in order.
 * headerFrame -> commentFrame -> dataFrame.
 * @author taktod
 */
public class SpeexFrameSelector extends AudioSelector {
	/** headerFrame */
	private HeaderFrame headerFrame = null;
	/** commentFrame */
	private CommentFrame commentFrame = null;
	/**
	 * set the header frame.
	 * (set from out side)
	 * ex: flv has only one kind of header.
	 * flv doesn't have specific byte informaton on it.
	 * @param frame
	 */
	public void setHeaderFrame(HeaderFrame frame) {
		this.headerFrame = frame;
	}
	/**
	 * set the comment frame.
	 * same as header frame.
	 * @param frame
	 */
	public void setCommentFrame(CommentFrame frame) {
		this.commentFrame = frame;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public IUnit select(IReadChannel channel) throws Exception {
		if(channel.position() == channel.size()) {
			return null;
		}
		SpeexFrame frame = null;
		if(headerFrame == null) {
			// try to get header frame.
			frame = new HeaderFrame();
			headerFrame = (HeaderFrame)frame;
		}
		else if(commentFrame == null) {
			// next try to get comment frame.
			frame = new CommentFrame();
			commentFrame = (CommentFrame)frame;
		}
		else {
			// treat as frame.
			frame = new Frame();
			frame.setHeaderFrame(headerFrame);
		}
		frame.minimumLoad(channel);
		return frame;
	}
}
