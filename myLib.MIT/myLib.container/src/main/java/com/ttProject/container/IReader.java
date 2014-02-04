package com.ttProject.container;

import java.util.List;

import com.ttProject.nio.channels.IReadChannel;

/**
 * ファイル読み込み
 * @author taktod
 */
public interface IReader {
	/**
	 * 読み込み動作
	 * @param channel
	 * @return
	 * @throws Exception
	 */
	public IContainer read(IReadChannel channel) throws Exception;
	/**
	 * 残っているデータを取得する動作
	 * @return
	 * @throws Exception
	 */
	public List<IContainer> getRemainData() throws Exception;
}
