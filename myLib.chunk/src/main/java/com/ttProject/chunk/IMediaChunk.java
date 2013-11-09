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
	 * データを追加します。
	 * @return
	 */
	public boolean write(ByteBuffer data);
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
}
