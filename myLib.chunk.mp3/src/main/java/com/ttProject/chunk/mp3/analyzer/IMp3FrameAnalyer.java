package com.ttProject.chunk.mp3.analyzer;

import com.ttProject.chunk.mp3.Mp3DataList;
import com.ttProject.media.Unit;

/**
 * unitの中身を解析して必要なmp3Frameを応答する動作
 * @author taktod
 */
public interface IMp3FrameAnalyer {
	/**
	 * mp3DataListをセットします
	 * @param mp3DataList
	 */
	public void setMp3DataList(Mp3DataList mp3DataList);
	/**
	 * 解析を実行します
	 * @param unit
	 */
	public void analyze(Unit unit);
}
