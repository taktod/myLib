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
 * amf0
 * @author taktod
 * @see http://download.macromedia.com/pub/labs/amf/amf0_spec_121207.pdf
 * Number		0x00 8byte doublebits
 * Boolean		0x01 1byte 0x01:true 0x00:false
 * String		0x02 2byte(size) data
 * Object		0x03 ([2byte(size) datatype data] x num) 00 00 09(eof)
 * MovieClip	0x04 ;reserved(unsupported.)
 * Null			0x05 
 * Undefined	0x06
 * Reference	0x07 2byte(ref value.)
 * Map			0x08 4byte(int(num?)) ([2byte(size) dataType data] x num) [00 00 2byte(size0)] 09(eof)
 * ObjectEnd	0x09
 * Array		0x0A ([type data] x num) [00 00 2byte(size0)] 09(eof)
 * Date			0x0B 8byte(doubleBits(unixtime)) 2byte(timezone?)
 * LongString	0x0C 4byte(size) data
 * Unsupported	0x0D
 * RecordSet	0x0E ;reserved(unsupported.)
 * XmlDocument	0x0F
 * TypedObject	0x10
 * AMF3Object	0x11
 */
public class Amf0Value {
	/**
	 * data type
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
		ObjectEnd(0x09), // impossible on the beginning.
		Array(0x0A),
		Date(0x0B),
		LongString(0x0C),
		Unsupported(0x0D),
		RecordSet(0x0E),
		XmlDocument(0x0F),
		TypedObject(0x10),
		Amf3Object(0x11);
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
			throw new RuntimeException("unanalyzable data." + value);
		}
	}
	/**
	 * get object from readChannel
	 * @param source
	 * @return
	 */
	public static Object getValueObject(IReadChannel source) throws Exception {
		ByteBuffer data = null;
		// first byte decide the type.
		Type type = Type.getType(BufferUtil.safeRead(source, 1).get());
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
					throw new Exception("the end of object is corrupted.");
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
					throw new Exception("the end of map is corrupted.");
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
		case Amf3Object:
			{
				// treat as Amf3
				return Amf3Value.getValueObject(source);
			}
		default:
			throw new Exception("unknown data.:" + type);
		}
	}
	/**
	 * get amf0Buffer from object.
	 * @param data
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static ByteBuffer getValueBuffer(Object data) throws Exception {
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
	 * string
	 * @param data
	 * @return
	 */
	private static ByteBuffer getStringBuffer(String data) {
		byte[] dat = data.getBytes();
		ByteBuffer buffer = ByteBuffer.allocate(dat.length + 3);
		// flg
		buffer.put((byte)0x02);
		// length
		buffer.putShort((short)dat.length);
		// data
		buffer.put(dat);
		buffer.flip();
		return buffer;
	}
	/**
	 * boolean
	 * @param data
	 * @return
	 */
	private static ByteBuffer getBooleanBuffer(Boolean data) {
		ByteBuffer buffer = ByteBuffer.allocate(2);
		// flg
		buffer.put((byte)0x01);
		// data
		buffer.put((byte)(data ? 1 : 0));
		buffer.flip();
		return buffer;
	}
	/**
	 * number
	 * @param num
	 * @return
	 */
	private static ByteBuffer getNumberBuffer(Number num) {
		ByteBuffer buffer = ByteBuffer.allocate(9);
		// flg
		buffer.put((byte)0x00);
		// data
		buffer.putLong(Double.doubleToLongBits(num.doubleValue()));
		buffer.flip();
		return buffer;
	}
	/**
	 * date
	 * @param date
	 * @return
	 */
	private static ByteBuffer getDateBuffer(Date date) {
		ByteBuffer buffer = ByteBuffer.allocate(11);
		// flg
		buffer.put((byte)0x0B);
		// unixtime
		buffer.putLong(Double.doubleToLongBits(date.getTime()));
		// timezone(fill with 0 for temp.)
		buffer.putShort((short)0);
		buffer.flip();
		return buffer;
	}
	/**
	 * array
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
	 * object(map)
	 * @param data
	 * @return
	 * @throws Exception
	 */
	private static ByteBuffer getObjectBuffer(Amf0Object<String, Object> data) throws Exception {
		List<ByteBuffer> amfDataList = new ArrayList<ByteBuffer>();
		int length = 0;
		for(Entry<String, Object> entry : data.entrySet()) {
			ByteBuffer amfData = makeMapElementBuffer(entry.getKey(), entry.getValue());
			length += amfData.remaining();
			amfDataList.add(amfData);
		}
		// find the byte, by calcurating elements.
		ByteBuffer buffer = ByteBuffer.allocate(length + 1 + 3);
		// flg
		buffer.put((byte)0x03);
		// data.
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
	 * Map
	 * @param data
	 * @return
	 * @throws Exception
	 */
	private static ByteBuffer getMapBuffer(Map<String, Object> data) throws Exception {
		// inside
		List<ByteBuffer> amfDataList = new ArrayList<ByteBuffer>();
		int length = 0;
		for(Entry<String, Object> entry : data.entrySet()) {
			ByteBuffer amfData = makeMapElementBuffer(entry.getKey(), entry.getValue());
			length += amfData.remaining();
			amfDataList.add(amfData);
		}
		// get the size, by calcurating elements.
		ByteBuffer buffer = ByteBuffer.allocate(length + 5 + 3);
		// flg
		buffer.put((byte)0x08);
		// size
		buffer.putInt(amfDataList.size());
		// body
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
	 * map element private func.
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
