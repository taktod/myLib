package com.ttProject.chunk.mpegts.analyzer;

import com.ttProject.chunk.mpegts.AudioDataList;
import com.ttProject.chunk.mpegts.VideoDataList;
import com.ttProject.media.Unit;

/**
 * unitの中身を解析して必要なPesもしくはIAudioDataに直す動作
 * @author taktod
 */
public interface IPesAnalyzer {
	/**
	 * videoDataListを設定します
	 * @param videoDataList
	 */
	public void setVideoDataList(VideoDataList videoDataList);
	/**
	 * audioDataListを設定します
	 * @param audioDataList
	 */
	public void setAudioDataList(AudioDataList audioDataList);
	/**
	 * 解析を実行します
	 * @param unit
	 */
	public void analyze(Unit unit, long timestamp);
}
