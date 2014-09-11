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
 * audioフレーム解析動作ベース
 * @author taktod
*/
public abstract class AudioAnalyzer implements IAnalyzer {
	/** ロガー */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(AudioAnalyzer.class);
	/** audioデータ選択オブジェクト */
	private AudioSelector selector;
	/**
	 * コンストラクタ
	 * @param selector
	 */
	public AudioAnalyzer(AudioSelector selector) {
		setSelector(selector);
	}
	/**
	 * セレクター参照
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
