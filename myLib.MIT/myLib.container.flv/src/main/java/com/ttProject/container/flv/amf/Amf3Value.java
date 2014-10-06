/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.flv.amf;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.util.BufferUtil;

/**
 * amf3
 * @author taktod
 */
public class Amf3Value {
	/**
	 * type
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
			throw new RuntimeException("unanalyzable data.:" + value);
		}
	}
	/**
	 * get object from IReadChannel
	 * @param source
	 * @return
	 * @throws Exception
	 */
	public static Object getValueObject(IReadChannel source) throws Exception {
		ByteBuffer data = null;
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
				data = BufferUtil.safeRead(source, 2);
				if(data.get() != 0x0B) {
					throw new Exception("unknwon code for object.");
				}
				if(data.get() != 0x01) {
					throw new Exception("start code of object is unexpect value.");
				}
				Map<String, Object> result = new HashMap<String, Object>();
				// load data.
				byte b;
				while((b = BufferUtil.safeRead(source, 1).get()) != 0x01) {
					if((b & 0x01) == 0) {
						throw new Exception("reference base object is not coded yet.");
					}
					data = BufferUtil.safeRead(source, b >>> 1);
					String key = new String(data.array()).intern();
					result.put(key, getValueObject(source));
				}
				return result;
			}
		case String:
			{
				byte b = BufferUtil.safeRead(source, 1).get();
				if((b & 0x01) != 0x01) {
					throw new RuntimeException("reference base object is not coded yet for string.");
				}
				data = BufferUtil.safeRead(source, b >>> 1);
				return new String(data.array()).intern();
			}
		case Undefined:
			break;
		default:
			throw new Exception("unanalyzable data.:" + type);
		}
		return null;
	}
	/**
	 * get u29 integer.
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
