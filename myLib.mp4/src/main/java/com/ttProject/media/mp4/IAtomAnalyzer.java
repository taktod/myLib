package com.ttProject.media.mp4;

import com.ttProject.nio.channels.IReadChannel;

/**
 * atomの解析処理
 * @author taktod
 */
public interface IAtomAnalyzer {
	/**
	 * 解析動作
	 * @param ch
	 * @return
	 * @throws Exception
	 */
	public Atom analyze(IReadChannel ch) throws Exception;
}
