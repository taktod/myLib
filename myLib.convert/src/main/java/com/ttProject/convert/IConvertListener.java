package com.ttProject.convert;

import java.nio.ByteBuffer;

/**
 * コンバート処理の出力を受け取る動作
 * @author taktod
 */
public interface IConvertListener {
	/**
	 * 出力データをうけとる
	 * @param buffer
	 */
	public void receiveData(ByteBuffer buffer);
}
