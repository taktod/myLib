package com.ttProject.convert;

import java.nio.ByteBuffer;

/**
 * コンバートする動作のマネージャー
 * @author taktod
 *
 */
public interface IConvertManager {
	/**
	 * データを渡す
	 * @param buffer
	 */
	public void applyData(ByteBuffer buffer);
	/**
	 * 開始する
	 */
	public void start();
	/**
	 * 閉じる
	 */
	public void close();
}
