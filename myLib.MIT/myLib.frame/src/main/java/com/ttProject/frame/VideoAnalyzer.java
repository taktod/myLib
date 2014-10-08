/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame;

import com.ttProject.nio.channels.IReadChannel;

/**
 * base analyzer for videoFrame
 * @author taktod
 */
public abstract class VideoAnalyzer implements IAnalyzer {
	/** select object for video */
	private VideoSelector selector;
	/**
	 * constructor
	 * @param selector
	 */
	public VideoAnalyzer(VideoSelector selector) {
		setSelector(selector);
	}
	/**
	 * ref the video Selector.
	 * @return
	 */
	public VideoSelector getSelector() {
		return selector;
	}
	/**
	 * set the video Selector.
	 * @param selector
	 */
	public void setSelector(VideoSelector selector) {
		this.selector = selector;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public IFrame analyze(IReadChannel channel) throws Exception {
		IFrame frame = (IFrame)selector.select(channel);
		if(frame != null) {
			frame.load(channel);
		}
		return frame;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public IFrame getRemainFrame() throws Exception {
		return null;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setPrivateData(IReadChannel channel) throws Exception {
		
	}
}
