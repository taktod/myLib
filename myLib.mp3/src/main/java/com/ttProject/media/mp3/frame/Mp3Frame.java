package com.ttProject.media.mp3.frame;

import com.ttProject.media.mp3.Frame;
import com.ttProject.media.mp3.IFrameAnalyzer;
import com.ttProject.nio.channels.IFileReadChannel;

/**
 * mp3のベースフレーム
 * @author taktod
 * 良く考えたらframeSizeは、header情報から生成する感じだったはず・・・うーん
 */
public class Mp3Frame extends Frame {
	/**
	 * 位置から計算する必要あり
	 */
	public Mp3Frame() {
		super(0, 0);
	}
	@Override
	public void analyze(IFileReadChannel ch, IFrameAnalyzer analyzer)
			throws Exception {
		
	}
}
