/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
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
	/**
	 * 処理途上のデータを応答します
	 * @return
	 * @throws Exception
	 */
	public IFrame getRemainFrame() throws Exception;
	/**
	 * 動作コーデックtypeを応答する
	 * @return
	 */
	public CodecType getCodecType();
	/**
	 * 該当コーデック用のprivateDataを設定する
	 * @param channel
	 * @throws Exception
	 */
	public void setPrivateData(IReadChannel channel) throws Exception;
}
