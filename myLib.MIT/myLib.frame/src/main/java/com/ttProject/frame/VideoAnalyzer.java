package com.ttProject.frame;

import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.IAnalyzer;
import com.ttProject.unit.IUnit;

/**
 * 映像フレームを解析する動作
 * @author taktod
 */
public abstract class VideoAnalyzer implements IAnalyzer {
	/** 映像解析のデータ選択オブジェクト */
	private final VideoSelector selector;
	/**
	 * コンストラクタ
	 * @param selector
	 */
	public VideoAnalyzer(VideoSelector selector) {
		this.selector = selector;
	}
	/**
	 * 映像解析セレクター参照
	 * @return
	 */
	public VideoSelector getSelector() {
		return selector;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public IUnit analyze(IReadChannel channel) throws Exception {
		IUnit unit = selector.select(channel);
		if(unit != null) {
			unit.load(channel);
		}
		return unit;
	}
}
