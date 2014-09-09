/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.chunk;

import java.nio.ByteBuffer;

/**
 * Mediaデータの塊のデータ
 * @author taktod
 */
public interface IMediaChunk {
	/**
	 * ヘッダー用のデータであるか
	 * @return
	 */
	public boolean isHeader();
	/**
	 * データを追加します。(追加書き込みしておく)
	 * @return
	 */
	public boolean write(ByteBuffer data);
	/**
	 * timestamp値を応答する(とりあえずmpegtsのptsでいってみる。)
	 * @return
	 */
	public long getTimestamp();
	/**
	 * このデータのduration値を参照する
	 * @return
	 */
	public float getDuration();
	/**
	 * 登録されている生データを参照します。
	 * @return
	 */
	public byte[] getRawData();
	/**
	 * 登録されている生データのbufferを参照します。
	 * @return
	 */
	public ByteBuffer getRawBuffer();
}
