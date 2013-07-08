package com.ttProject.media.mkv;

import com.ttProject.nio.channels.IReadChannel;

/**
 * Elementの解析処理
 * @author taktod
 */
public interface IElementAnalyzer {
	/**
	 * 解析動作
	 * @param ch
	 * @return
	 * @throws Exception
	 */
	public Element analyze(IReadChannel ch) throws Exception;
}
