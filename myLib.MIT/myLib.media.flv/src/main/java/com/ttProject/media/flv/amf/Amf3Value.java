/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.media.flv.amf;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.util.BufferUtil;

/**
 * amf3のデータを扱うクラス
 * @author taktod
 */
public class Amf3Value {
	/**
	 * データタイプ
	 */
	public enum Type {
		Undefined(0x00),
		Null(0x01),
		False(0x02),
		True(0x03),
		Integer(0x04),
		Double(0x05),
		String(0x06),
		XmlDoc(0x07),
		Date(0x08),
		Array(0x09),
		Object(0x0A),
		Xml(0x0B),
		ByteArray(0x0C),
		VectorInt(0x0D),
		VectorUint(0x0E),
		VectorDouble(0x0F),
		VectorObject(0x10),
		Dictionary(0x11);
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
	 * ファイルからデータを呼び出してオブジェクト化していく
	 * @param source
	 * @return
	 * @throws Exception
	 */
	public static Object getValueObject(IReadChannel source) throws Exception {
		ByteBuffer data = null;
		// 先頭のデータを読み込みます。
		Type type = Type.getType(BufferUtil.safeRead(source, 1).get());
		switch(type) {
		case Null:
			return null;
		case False:
			return false;
		case True:
			return true;
		case Integer:
			return getU29Integer(source);
		case Double:
			return Double.longBitsToDouble(BufferUtil.safeRead(source, 8).getLong());
		case Object:
			{
				// 次のバイトは0x0Bであることを期待します。しらないデータがでた場合は、あとで解析してプログラムを合わせる予定
				data = BufferUtil.safeRead(source, 2);
				if(data.get() != 0x0B) {
					throw new Exception("知らないコードがきました。");
				}
				if(data.get() != 0x01) {
					throw new Exception("objectの始点がおかしいです。");
				}
				Map<String, Object> result = new HashMap<String, Object>();
				// dataを読み込む
				byte b;
				while((b = BufferUtil.safeRead(source, 1).get()) != 0x01) {
					if((b & 0x01) == 0) {
						throw new Exception("リファレンスベースの動作はまだ作成していません。");
					}
					data = BufferUtil.safeRead(source, b >>> 1);
					String key = new String(data.array()).intern();
					// AMF3のデータではいっているので、取得する。
					result.put(key, getValueObject(source));
				}
				return result;
			}
		case String:
			{
				byte b = BufferUtil.safeRead(source, 1).get();
				if((b & 0x01) != 0x01) {
					throw new RuntimeException("参照値は未定義");
				}
				data = BufferUtil.safeRead(source, b >>> 1);
				return new String(data.array()).intern();
			}
		case Undefined:
			break;
		default:
			throw new Exception("解析不能なデータ:" + type);
		}
		// データを解析していく
		return null;
	}
	/**
	 * U29のデータを解析します
	 * @param source
	 * @return
	 * @throws Exception
	 */
	public static int getU29Integer(IReadChannel source) throws Exception {
		byte b = BufferUtil.safeRead(source, 1).get();
		int data = 0;
		while((b & 0x80) != 0x00) {
			data = (data << 7) + (b & 0x7F);
			b = BufferUtil.safeRead(source, 1).get();
		}
		data = (data << 7) + (b & 0x7F);
		return data;
	}
}

/*
 * 11 0A 0B 01(クラス名:なし)
 * 03(参照なしの文字列１) 63(A) 
 * 0A(object) 01(クラス名なし？) [03 41 文字Aのキー] [04 01(int 1)] 01 03 62 06 05 31 34 03 61 04 0D 01 
 * 
 * 00 02 00 0A 6F 6E 4D 65 74 61 44 61 74 61 
 * 11 0A 0B 01 03 63 
 * 0A 03 01 03 62 06 05 31 34 03 61 04 0D 01
 */
