package com.ttProject.frame.adpcmimawav.test.rangecoder;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * rangeCoderを実施するためのクラス
 * @author taktod
 */
public class RangeCoder {
	/** ロガー */
	private Logger logger = Logger.getLogger(RangeCoder.class);
	// indexからデータ値を求めるたまえのリスト
	private List<Integer> dataList = new ArrayList<Integer>(); // 対応しているデータリスト
	// データ値からindexを求めるためのmap
	private Map<Integer, Integer> inverseMap = new HashMap<Integer, Integer>();
	// indexからweight値を知るためのリスト
	private List<Integer> weightList = new ArrayList<Integer>(); // データのweightリスト
	// indexから下限を知るためのリスト
	private List<Integer> minBorderList = new ArrayList<Integer>(); // 下限リスト
	
	private int low; // 下限(エンコード用)
	private int code; // code(デコード用)
	private int range; // range
	private int weight; // 振り分け値の合計
	private final int rangeMax;
	private final int rangeBorder;

	/** 処理結果 */
	private List<Byte> target = new ArrayList<Byte>();

	/**
	 * コンストラクタ
	 */
	public RangeCoder(int rangeMax, int rangeBorder) {
		this.rangeMax = rangeMax;
		this.rangeBorder = rangeBorder;
		low = 0;
		range = this.rangeMax;
	}
	public RangeCoder() {
		this(0x01000000, 0x010000);
	}
	
	/**
	 * テーブルを作ります
	 * a:値 b:weight
	 * [
	 *  [a b],
	 *  [a b],
	 *  [a b],
	 *  [a b],
	 *  [a b],
	 *  [a b],
	 *  [a b]
	 * ]
	 * として定義します
	 */
	public void setupTable(Integer[][] data) {
		weight = 0;
		int i = 0;
		for(Integer[] dat : data) {
			inverseMap.put(dat[0], i);
			dataList.add(dat[0]);
			weightList.add(dat[1]);
			minBorderList.add(weight);
			weight += dat[1];
			i ++;
		}
	}
	// デコードターゲット設定
	public void setDecodeTarget(ByteBuffer buffer) {
		while(buffer.remaining() > 0) {
			target.add(buffer.get());
		}
		// 初期コードを読み込んでおく。
		code = 0;
		code = code << 8 | (target.remove(0) & 0xFF);
		code = code << 8 | (target.remove(0) & 0xFF);
		code = code << 8 | (target.remove(0) & 0xFF);
		logger.info(Integer.toHexString(code));
	}
	/**
	 * 
	 * @return -1だったらデータなし
	 */
	public Integer decodeData() {
		int pos = code * weight / range;
		logger.info("pos:" + pos);
		int val = 0;
		for(int i = 0;i < dataList.size();i ++) {
			if(minBorderList.get(i) <= pos &&  pos < minBorderList.get(i) + weightList.get(i)) {
				val = dataList.get(i);
				code = code - range * minBorderList.get(i) / weight;
				range = range * weightList.get(i) / weight;
				logger.info(Integer.toHexString(code) + " " + Integer.toHexString(range));
				if(range < rangeBorder) {
					if(target.size() < 0) {
						return -1; // データがなくなったら-1を応答する
					}
					range = range * 0x0100;
					code = code << 8 | (target.remove(0) & 0xFF);
				}
				return val;
			}
		}
		logger.info("データがなくなったので、強制おわり");
		return -1;
	}
	// デコード処理
	// エンコード処理
	public void encodeData(int d) {
		low += range * minBorderList.get(inverseMap.get(d)) / weight;
		range = range * weightList.get(inverseMap.get(d)) / weight;
		if(range < rangeBorder) {
			range *= 0x0100; // 8bitシフト
			target.add((byte)((low & 0xFF0000) >> 16));
			low = (low & 0x00FFFF) * 0x0100;
		}
		logger.info(Integer.toHexString(low) + " " + Integer.toHexString(range));
	}
	// エンコード結果
	public ByteBuffer getEncodeResult() {
		// のこっているlowをそのまま追加する。
		target.add((byte)((low & 0xFF0000) >> 16));
		target.add((byte)((low & 0x00FF00) >> 8));
		target.add((byte)((low & 0x0000FF)));
		ByteBuffer result = ByteBuffer.allocate(target.size());
		for(Byte b : target) {
			result.put(b);
		}
		result.flip();
		return result;
	}
}
