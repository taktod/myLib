/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame;

import com.ttProject.nio.channels.IReadChannel;

/**
 * audioフレーム解析動作ベース
 * @author taktod
*/
public abstract class AudioAnalyzer implements IAnalyzer {
	/** audioデータ選択オブジェクト */
	private final AudioSelector selector;
	/**
	 * コンストラクタ
	 * @param selector
	 */
	public AudioAnalyzer(AudioSelector selector) {
		this.selector = selector;
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
	 */
	@Override
	public IFrame analyze(IReadChannel channel) throws Exception {
		IFrame frame = (IFrame)selector.select(channel);
		if(frame != null) {
			frame.load(channel);
		}
		return frame;
	}
	@Override
	public IFrame getRemainFrame() throws Exception {
		return null;
	}
}
