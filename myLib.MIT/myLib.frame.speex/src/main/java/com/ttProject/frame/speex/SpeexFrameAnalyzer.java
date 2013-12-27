package com.ttProject.frame.speex;

import com.ttProject.frame.AudioAnalyzer;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.IUnit;

/**
 * speexデータ解析
 * @author taktod
 */
public class SpeexFrameAnalyzer extends AudioAnalyzer {
	private SpeexFrame tmpFrame = null;
	/**
	 * コンストラクタ
	 */
	public SpeexFrameAnalyzer() {
		super(new SpeexFrameSelector());
	}
	/**
	 * フレームが読み込み途上だったらそっちの続き読み込みを実施しなければいけないので、analyzeを別途作成します。
	 */
	@Override
	public IUnit analyze(IReadChannel channel) throws Exception {
		SpeexFrame unit = null;
		if(tmpFrame != null) {
			unit = tmpFrame;
			unit.load(channel);
		}
		else {
			unit = (SpeexFrame)super.analyze(channel);
		}
		// データを評価する
		if(!unit.isComplete()) {
			tmpFrame = unit;
		}
		else {
			tmpFrame = null;
		}
		return unit;
	}
}
