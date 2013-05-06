package com.ttProject.media.mp4;

import com.ttProject.nio.channels.IFileReadChannel;

/**
 * atomの解析処理
 * @author taktod
 */
public interface IAtomAnalyzer {
	public Atom analyze(IFileReadChannel ch) throws Exception;
}
