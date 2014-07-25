package com.ttProject.frame.adpcmimawav.test;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ttProject.util.HexUtil;

/**
 * ちょっとrangeCoderを書いてみる。
 * @author taktod
 */
public class RangeCoderTest {
	/** ロガー */
	private Logger logger = Logger.getLogger(RangeCoderTest.class);
	// とりあえずテストでは次のようにする。
	private Map<Integer, Integer> map = new HashMap<Integer, Integer>();
	private Map<Integer, Integer> map2 = new HashMap<Integer, Integer>();
	private int weight = 0;
	{
		map2.put(0x0, 4);
		map2.put(0x1, 4);
		map2.put(0x2, 4);
		map2.put(0x3, 3);
		map2.put(0x4, 3);
		map2.put(0x5, 3);
		map2.put(0x6, 3);
		map2.put(0x7, 2);
		map2.put(0x8, 2);
		map2.put(0x9, 2);
		map2.put(0xA, 2);
		map2.put(0xB, 2);
		map2.put(0xC, 1);
		map2.put(0xD, 1);
		map2.put(0xE, 1);
		map2.put(0xF, 1);
/*		map2.put(0xA, 4);
		map2.put(0xB, 2);
		map2.put(0xC, 1);
		map2.put(0xD, 1);*/
		for(Entry<Integer, Integer> entry : map2.entrySet()) {
			map.put(entry.getKey(), weight);
			weight += entry.getValue();
		}
		logger.info(map);
		logger.info(map2);
	}
	/*
	 * 0 1 2 3 4 5 6 7 8 9 a b c d e f
	 * 4 4 4 3 3 3 3 2 2 2 2 2 1 1 1 1
	 * よって出現率は次のようにする。
	 * 合計で12 + 12 + 10 + 4 = 38
	 *        0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f
	 * 出現率  4  4  4  3  3  3  3  2  2  2  2  2  1  1  1  1 (1/38)
	 * 下限値  0  4  8 12 15 18 21 24 26 28 30 32 34 35 36 37
	 * 上限値  4  8 12 15 18 21 24 26 28 30 32 34 35 36 37 38
	 * 0x0, 0x1000000でやってみる
	 */
	@Test
	public void test() throws Exception {
		logger.info("開始");
		// とりあえず0x12345678123412341234123411111111を圧縮してみることにする。
		ByteBuffer data = HexUtil.makeBuffer("123456");
		logger.info(HexUtil.toHex(data));
		// range圧縮する。
		// 始め
		// [0, 0x01000000]
		int low, range;
		low = 0;
		range = 0x01000000;
		logger.info(Integer.toHexString(low) + " " + Integer.toHexString(range));
		List<Byte> foundList = new ArrayList<Byte>();
		while(data.remaining() > 0) {
			byte b = data.get();
			int bit = ((b & 0xFF) >>> 4);
			low += range * map.get(bit) / weight;
			range = range * map2.get(bit) / weight;
			if(range < 0x010000) {
				range *= 0x0100;
				foundList.add((byte)((low & 0xFF0000) >> 16));
				low = (low & 0x00FFFF) * 0x0100;
			}
			logger.info(Integer.toHexString(low) + " " + Integer.toHexString(range));
			bit = (b & 0x0F);
			low += range * map.get(bit) / weight;
			range = range * map2.get(bit) / weight;
			if(range < 0x010000) {
				range *= 0x0100;
				foundList.add((byte)((low & 0xFF0000) >> 16));
				low = (low & 0x00FFFF) * 0x0100;
			}
			logger.info(Integer.toHexString(low) + " " + Integer.toHexString(range));
		}
		foundList.add((byte)((low & 0xFF0000) >> 16));
		foundList.add((byte)((low & 0x00FF00) >> 8));
		foundList.add((byte)((low & 0x0000FF)));
		byte[] result = new byte[foundList.size()];
		for(int i = 0;i < foundList.size();i ++) {
			result[i] = foundList.get(i);
		}
		logger.info(HexUtil.toHex(result));
	}
	@Test
	public void decodeTest() throws Exception {
		logger.info("decode開始");
		// FA 82 80 00 00
//		byte[] data = {(byte)0xFA, (byte)0x80, (byte)0x00, (byte)0x00};
//		byte[] data = {(byte)0xFA, (byte)0x82, (byte)0x80, (byte)0x00, (byte)0x00};
		ByteBuffer data = HexUtil.makeBuffer("219C20876B");
//		byte[] data = {(byte)0x6B, (byte)0xCE, (byte)0xE8, (byte)0x96, (byte)0x68, (byte)0x40, (byte)0x70, (byte)0x0D};
		int code, range;
		code = 0;
		// とりあえず先頭の３つ読み込む
		code = code << 8 | (data.get() & 0xFF);
		code = code << 8 | (data.get() & 0xFF);
		code = code << 8 | (data.get() & 0xFF);
		logger.info(Integer.toHexString(code));
		range = 0x1000000;
		while(true) {
			int pos = code * weight / range;
			// 見つける
			int val = 0;
			for(Entry<Integer, Integer> entry : map.entrySet()) {
				if(entry.getValue() >= pos || entry.getValue() + map2.get(entry.getKey()) > pos) {
					val = entry.getKey();
					System.out.print(Integer.toHexString(val));
					break;
				}
			}
			code = code - range * map.get(val) / weight;
			range = range * map2.get(val) / weight;
			if(range < 0x010000) {
				if(data.remaining() == 0) {
					break;
				}
				range = range * 0x0100;
				code = code << 8 | (data.get() & 0xFF);
			}
		}
		System.out.println();
	}
}
