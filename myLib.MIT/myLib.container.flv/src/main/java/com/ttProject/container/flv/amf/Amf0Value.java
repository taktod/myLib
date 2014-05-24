/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.flv.amf;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.util.BufferUtil;

/**
 * amf0のデータを扱うクラス
 * @author taktod
 * @see http://download.macromedia.com/pub/labs/amf/amf0_spec_121207.pdf
 * Number		0x00 8バイト doublebits
 * Boolean		0x01 1バイト 0x01:true 0x00:false
 * String		0x02 2バイト(サイズ) データ
 * Object		0x03 [2バイト(サイズ) データ 型タイプ 型データ] x 要素数分 00 00 09(eof)
 * MovieClip	0x04 ;予約済みで未サポート
 * Null			0x05 
 * Undefined	0x06
 * Reference	0x07 2バイト(参照値)
 * Map			0x08 4バイト(intデータ(要素数？)) [2バイト(サイズ) データ 型タイプ 型データ] x 要素数分 [00 00 2バイト(サイズ0)] 09(eof)
 * ObjectEnd	0x09
 * Array		0x0A [型タイプ 型データ] x 要素数分 [00 00 2バイト(サイズ0)] 09(eof)
 * Date			0x0B 8バイト(doubleBits(unixtime)) 2バイト(timezone?)
 * LongString	0x0C 4バイト(サイズ) データ
 * Unsupported	0x0D
 * RecordSet	0x0E ;予約済みで未サポート
 * XmlDocument	0x0F
 * TypedObject	0x10
 */
public class Amf0Value {
	/**
	 * データタイプ
	 */
	public enum Type {
		Number(0x00),
		Boolean(0x01),
		String(0x02),
		Object(0x03),
		MovieClip(0x04),
		Null(0x05),
		Undefined(0x06),
		Reference(0x07),
		Map(0x08),
		ObjectEnd(0x09), // これ先頭にこないっぽい。
		Array(0x0A),
		Date(0x0B),
		LongString(0x0C),
		Unsupported(0x0D),
		RecordSet(0x0E),
		XmlDocument(0x0F),
		TypedObject(0x10);
		private final int value;
		private Type(int value) {
			this.value = value;
		}
		public int intValue() {
			return value;
		}
		public static Type getType(int value) {
			for(Type t : values()) {
				if(t.intValue() == value) {
					return t;
				}
			}
			throw new RuntimeException("解析不能なデータでした。" + value);
		}
	}
	/**
	 * ファイルからデータを呼び出してオブジェクト化していく。
	 * @param source
	 * @return
	 */
	public static Object getValueObject(IReadChannel source) throws Exception {
		ByteBuffer data = null;
		Type type = Type.getType(BufferUtil.safeRead(source, 1).get());
		// 先頭の１バイトを読み込む
		switch(type) {
		case Number:
			{
				data = BufferUtil.safeRead(source, 8);
				return Double.longBitsToDouble(data.getLong());
			}
		case Boolean:
			{
				return BufferUtil.safeRead(source, 1).get() != 0x00;
			}
		case String:
			{
				int length = BufferUtil.safeRead(source, 2).getShort();
				data = BufferUtil.safeRead(source, length);
				return new String(data.array()).intern();
			}
		case LongString:
			{
				int length = BufferUtil.safeRead(source, 4).getInt();
				data = BufferUtil.safeRead(source, length);
				return new String(data.array()).intern();
			}
		case Object:
			{
				Amf0Object<String, Object> object = new Amf0Object<String, Object>();
				int nameSize;
				while((nameSize = BufferUtil.safeRead(source, 2).getShort()) != 0) {
					data = BufferUtil.safeRead(source, nameSize);
					String key = new String(data.array()).intern();
					Object value = getValueObject(source);
					object.put(key, value);
				}
				if(Type.getType(BufferUtil.safeRead(source, 1).get()) != Type.ObjectEnd) {
					throw new Exception("objectの終端がおかしかったです。");
				}
				return object;
			}
		case Null:
		case Unsupported:
		case Undefined:
			{
				return null;
			}
		case Map:
			{
				Map<String, Object> map = new LinkedHashMap<String, Object>();
				/*int length = */BufferUtil.safeRead(source, 4).getInt();
				int nameSize;
				while((nameSize = BufferUtil.safeRead(source, 2).getShort()) != 0) {
					data = BufferUtil.safeRead(source, nameSize);
					String key = new String(data.array()).intern();
					Object value = getValueObject(source);
					map.put(key, value);
				}
				if(Type.getType(BufferUtil.safeRead(source, 1).get()) != Type.ObjectEnd) {
					throw new Exception("mapの終端がおかしかったです。");
				}
				return map;
			}
		case Array:
			{
				List<Object> array = new ArrayList<Object>();
				int length = BufferUtil.safeRead(source, 4).getInt();
				for(int i = 0;i < length;i ++) {
					array.add(getValueObject(source));
				}
				return array;
			}
		case Date:
			{
				data = BufferUtil.safeRead(source, 8);
				Date date = new Date((long)Double.longBitsToDouble(data.getLong()));
				BufferUtil.safeRead(source, 2); // timezone?
				return date;
			}
		default:
			throw new Exception("知らないデータがきました。:" + type);
		}
	}
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
		if(data instanceof Amf0Object<?, ?>) {
			return getObjectBuffer((Amf0Object<String, Object>)data);
		}
		if(data instanceof Map<?, ?>) {
			return getMapBuffer((Map<String, Object>)data);
		}
		if(data instanceof List<?>) {
			return getArrayBuffer((List<Object>)data);
		}
		if(data instanceof Date) {
			return getDateBuffer((Date) data);
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
	private static ByteBuffer getDateBuffer(Date data) {
		ByteBuffer buffer = ByteBuffer.allocate(11);
		// フラグ
		buffer.put((byte)0x0B);
		// unixtime
		buffer.putLong(Double.doubleToLongBits(data.getTime()));
		// timezone(とりあえず0でうめとく。)
		buffer.putShort((short)0);
		buffer.flip();
		return buffer;
	}
	/**
	 * 配列データ用の処理
	 * @param data
	 * @return
	 * @throws Exception
	 */
	private static ByteBuffer getArrayBuffer(List<Object> data) throws Exception {
		List<ByteBuffer> amfDataList = new ArrayList<ByteBuffer>();
		int length = 0;
		for(Object dat : data) {
			ByteBuffer amfData = getValueBuffer(dat);
			length += amfData.remaining();
			amfDataList.add(amfData);
		}
		ByteBuffer buffer = ByteBuffer.allocate(length + 1 + 4);
		buffer.put((byte)0x0A);
		buffer.putInt(amfDataList.size());
		for(ByteBuffer amfData : amfDataList) {
			buffer.put(amfData);
		}
		buffer.flip();
		return buffer;
	}
	/**
	 * ActionScriptのObject(map)用のBufferを取得
	 * @param data
	 * @return
	 * @throws Exception
	 */
	private static ByteBuffer getObjectBuffer(Amf0Object<String, Object> data) throws Exception {
		// 中身の準備
		List<ByteBuffer> amfDataList = new ArrayList<ByteBuffer>();
		int length = 0;
		for(Entry<String, Object> entry : data.entrySet()) {
			ByteBuffer amfData = makeMapElementBuffer(entry.getKey(), entry.getValue());
			length += amfData.remaining();
			amfDataList.add(amfData);
		}
		// 子要素を足していって、必要なサイズをみつける必要あり。
		ByteBuffer buffer = ByteBuffer.allocate(length + 1 + 3);
		// フラグ
		buffer.put((byte)0x03);
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
