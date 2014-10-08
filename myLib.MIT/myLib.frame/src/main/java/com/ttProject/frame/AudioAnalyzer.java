/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame;

import org.apache.log4j.Logger;

import com.ttProject.nio.channels.IReadChannel;

/**
 * base for audio frame analyze.
 * @author taktod
 */
public abstract class AudioAnalyzer implements IAnalyzer {
	/** logger */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(AudioAnalyzer.class);
	/** audioFrame selector */
	private AudioSelector selector;
	/**
	 * constructor
	 * @param selector
	 */
	public AudioAnalyzer(AudioSelector selector) {
		setSelector(selector);
	}
	/**
	 * ref the selector
	 * @return
	 */
	public AudioSelector getSelector() {
		return selector;
	}
	/**
	 * {@inheritDoc}
	 * @param selector
	 */
	protected void setSelector(AudioSelector selector) {
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
