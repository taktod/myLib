package com.ttProject.frame;

import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.IAnalyzer;
import com.ttProject.unit.IUnit;

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
	public IUnit analyze(IReadChannel channel) throws Exception {
		IUnit unit = selector.select(channel);
		if(unit != null) {
			unit.load(channel);
		}
		return unit;
	}
}
