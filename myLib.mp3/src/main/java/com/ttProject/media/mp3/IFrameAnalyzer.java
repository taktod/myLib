package com.ttProject.media.mp3;

import com.ttProject.nio.channels.IReadChannel;

/**
 * frameの解析処理(mp3には必要なさそうだけどね)
 * @author taktod
 */
public interface IFrameAnalyzer {
	/**
	 * 解析動作
	 * @param ch
	 * @return
	 * @throws Exception
	 */
	public Frame analyze(IReadChannel ch) throws Exception;
}
