package com.ttProject.chunk.mp3.analyzer;

import com.ttProject.chunk.mp3.Mp3DataList;
import com.ttProject.media.Unit;
import com.ttProject.media.mp3.frame.Mp3;

/**
 * mp3のframeをmp3DataListに格納していきます。
 * @author taktod
 */
public class Mp3FrameAnalyzer implements IMp3FrameAnalyer {
	/** mp3のデータ保持オブジェクト */
	private Mp3DataList mp3DataList;
	/**
	 * mp3DataListを設定します
	 */
	@Override
	public void setMp3DataList(Mp3DataList mp3DataList) {
		this.mp3DataList = mp3DataList;
	}
	/**
	 * 解析動作
	 */
	@Override
	public void analyze(Unit unit) {
		if(unit instanceof Mp3) {
			Mp3 mp3 = (Mp3)unit;
			mp3DataList.addMp3Data(mp3);
		}
	}
}
