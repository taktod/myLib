package com.ttProject.frame;

import com.ttProject.nio.channels.IReadChannel;

/**
 * 内部データを解析して応答する動作
 * ByteBufferから取り出すもの。(追記される可能性があるデータ stdinとか)
 * IReadChannelから取り出すもの。(固定されているデータ)
 * あたりがほしい。
 * @author taktod
 */
public interface IAnalyzer {
	/**
	 * 解析動作(全byteデータの確認を実施します)
	 * @param channel
	 * @return
	 * @throws Exception
	 */
	public IFrame analyze(IReadChannel channel) throws Exception;
}