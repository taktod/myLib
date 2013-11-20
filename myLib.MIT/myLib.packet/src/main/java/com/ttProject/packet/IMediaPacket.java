package com.ttProject.packet;

import java.nio.ByteBuffer;

/**
 * Mediaデータは要求された場合に、そのパケットのデータをファイルに書き出す機能をもっています。
 * @author taktod
 */
public interface IMediaPacket {
	/**
	 * headerパケットであるか応答する。
	 * @return true:header false:media
	 */
	public boolean isHeader();
	/**
	 * byteBufferの中身を解析します。
	 * @param buffer 解析するネタ
	 * @return true:解析完了パケットが書き込みReadyになっています。false:解析途上
	 */
	public boolean analize(ByteBuffer buffer);
	/**
	 * データをファイルに書き込みします。
	 * @param targetFile 書き込むファイル
	 * @param append appendmodeにするかどうか
	 */
	public void writeData(String targetFile, boolean append);
	/**
	 * packetの秒数を取得する。
	 * @return
	 */
	public float getDuration();
	/**
	 * 生データ参照用
	 * @return
	 */
	public byte[] getRawData();
}
