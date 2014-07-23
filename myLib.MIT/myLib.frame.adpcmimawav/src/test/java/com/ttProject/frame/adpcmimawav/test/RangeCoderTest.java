package com.ttProject.frame.adpcmimawav.test;

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
/*		map2.put(0x0, 4);
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
		map2.put(0xF, 1);*/
		map2.put(0xA, 4);
		map2.put(0xB, 2);
		map2.put(0xC, 1);
		map2.put(0xD, 1);
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
//		byte[] data = {0x12, 0x34, 0x56, 0x78, 0x12, 0x34, 0x56, 0x78, 0x12, 0x34, 0x12, 0x34, 0x12, 0x34, 0x12, 0x34, 0x11, 0x11, 0x11, 0x11, 0x11};
		byte[] data = {(byte)0xdc, (byte)0xbb, (byte)0xaa, (byte)0xaa, (byte)0xbb, (byte)0xaa, (byte)0xaa};
		logger.info(HexUtil.toHex(data, true));
		// range圧縮する。
		// 始め
		// [0, 0x01000000]
		int low, range;
		low = 0;
		range = 0x01000000;
		List<Byte> foundList = new ArrayList<Byte>();
		for(byte b : data) {
			int bit = ((b & 0xFF) >>> 4);
			logger.info(Integer.toHexString(bit));
			low += range * map.get(bit) / weight;
			range = range * map2.get(bit) / weight;
			if(range < 0x010000) {
				range *= 0x0100;
				foundList.add((byte)((low & 0xFF0000) >> 16));
				low = (low & 0x00FFFF) * 0x0100;
			}
			logger.info(Integer.toHexString(low) + " " + Integer.toHexString(range));
			bit = (b & 0x0F);
			logger.info(Integer.toHexString(bit));
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
		logger.info(HexUtil.toHex(result, true));
	}
	@Test
	public void decodeTest() throws Exception {
		logger.info("decode開始");
		// FA 82 80 00 00
//		byte[] data = {(byte)0xFA, (byte)0x80, (byte)0x00, (byte)0x00};
		byte[] data = {(byte)0xFA, (byte)0x82, (byte)0x80, (byte)0x00, (byte)0x00};
		List<Byte> list = new ArrayList<Byte>();
		for(byte b : data) {
			list.add(b);
		}
		int code, range;
		code = 0;
		int i = 0;
		// とりあえず先頭の３つ読み込む
		code = code << 8 | (list.get(i ++) & 0xFF);
		code = code << 8 | (list.get(i ++) & 0xFF);
		code = code << 8 | (list.get(i ++) & 0xFF);
		logger.info(Integer.toHexString(code));
		range = 0x1000000;
		while(true) {
			int pos = code * weight / range;
			// 見つける
			int val = 0;
			for(Entry<Integer, Integer> entry : map.entrySet()) {
				if(entry.getValue() >= pos || entry.getValue() + map2.get(entry.getKey()) > pos) {
					val = entry.getKey();
					logger.info(Integer.toHexString(val));
					break;
				}
			}
			code = code - range * map.get(val) / weight;
			range = range * map2.get(val) / weight;
			if(range < 0x010000) {
				if(i == list.size()) {
					break;
				}
				range = range * 0x0100;
				code = code << 8 | (list.get(i ++) & 0xFF);
			}
		}
	}
}
