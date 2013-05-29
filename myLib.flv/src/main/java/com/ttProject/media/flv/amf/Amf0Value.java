package com.ttProject.media.flv.amf;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * amf0のデータを扱うクラス
 * @author taktod
 * Number	0x00 8バイト doublebits
 * Boolean	0x01 1バイト 0x01:true 0x00:false
 * String	0x02 2バイト(サイズ) データ
 * Map		0x08 4バイト(intデータ(要素数？)) [2バイト(サイズ) データ 型タイプ 型データ] x 要素数分 00 00 09(eof)
 * Array	0x0A [2バイト(サイズ) データ 型タイプ 型データ] x 要素数分 00 00 09(eof)
 * LongString 0x0C 4バイト(サイズ) データ
 */
public class Amf0Value {
	/**
	 * 任意のオブジェクトをAMF0用のbyteBufferにする
	 * @param data
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static ByteBuffer getValueBuffer(Object data) throws Exception {
		// 入力データに対応したバイトデータをAMF0オブジェクトとして応答する。
		if(data instanceof String) {
			return getStringBuffer((String)data);
		}
		if(data instanceof Boolean) {
			return getBooleanBuffer((Boolean)data);
		}
		if(data instanceof Number) {
			return getNumberBuffer((Number)data);
		}
		if(data instanceof Map<?, ?>) {
			return getMapBuffer((Map<String, Object>)data);
		}
		throw new Exception("unknown amf0Data");
	}
	/**
	 * 文字列用の処理
	 * @param data
	 * @return
	 */
	private static ByteBuffer getStringBuffer(String data) {
		byte[] dat = data.getBytes();
		ByteBuffer buffer = ByteBuffer.allocate(dat.length + 3);
		// フラグ
		buffer.put((byte)0x02);
		// 長さ
		buffer.putShort((short)dat.length);
		// データ
		buffer.put(dat);
		buffer.flip();
		return buffer;
	}
	/**
	 * boolean用の処理
	 * @param data
	 * @return
	 */
	private static ByteBuffer getBooleanBuffer(Boolean data) {
		ByteBuffer buffer = ByteBuffer.allocate(2);
		// フラグ
		buffer.put((byte)0x01);
		// データ
		buffer.put((byte)(data ? 1 : 0));
		buffer.flip();
		return buffer;
	}
	/**
	 * 数値用の処理
	 * @param num
	 * @return
	 */
	private static ByteBuffer getNumberBuffer(Number num) {
		ByteBuffer buffer = ByteBuffer.allocate(9);
		// フラグ
		buffer.put((byte)0x00);
		// データ
		buffer.putLong(Double.doubleToLongBits(num.doubleValue()));
		buffer.flip();
		return buffer;
	}
	/**
	 * Map用の処理
	 * @param data
	 * @return
	 * @throws Exception
	 */
	private static ByteBuffer getMapBuffer(Map<String, Object> data) throws Exception {
		// 中身の準備
		List<ByteBuffer> amfDataList = new ArrayList<ByteBuffer>();
		int length = 0;
		for(Entry<String, Object> entry : data.entrySet()) {
			ByteBuffer amfData = makeMapElementBuffer(entry.getKey(), entry.getValue());
			length += amfData.remaining();
			amfDataList.add(amfData);
		}
		// 子要素を足していって、必要なサイズをみつける必要あり。
		ByteBuffer buffer = ByteBuffer.allocate(length + 5 + 3);
		// フラグ
		buffer.put((byte)0x08);
		// サイズ
		buffer.putInt(amfDataList.size());
		// 中身書き込み
		for(ByteBuffer amfData : amfDataList) {
			buffer.put(amfData);
		}
		// eof
		buffer.put((byte)0x00);
		buffer.put((byte)0x00);
		buffer.put((byte)0x09);
		buffer.flip();
		return buffer;
	}
	/**
	 * マップの内部データ用の処理
	 * @param name
	 * @param data
	 * @return
	 * @throws Exception
	 */
	private static ByteBuffer makeMapElementBuffer(String name, Object data) throws Exception {
		ByteBuffer amfData = Amf0Value.getValueBuffer(data);
		byte[] nameBytes = name.getBytes();
		ByteBuffer buffer = ByteBuffer.allocate(amfData.remaining() + nameBytes.length + 2);
		buffer.putShort((short)nameBytes.length);
		buffer.put(nameBytes);
		buffer.put(amfData);
		buffer.flip();
		return buffer;
	}
}
