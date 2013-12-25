package com.ttProject.container;

import com.ttProject.nio.channels.IReadChannel;

/**
 * ファイル読み込み
 * @author taktod
 */
public interface IReader {
	/**
	 * 読み込み動作(全データの確認を実施します。)
	 * @param channel
	 * @return
	 * @throws Exception
	 */
	public IContainer read(IReadChannel channel) throws Exception;
}
