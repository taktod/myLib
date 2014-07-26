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
	
	private int carryBuffer; // 最上位の桁の値
	private int carryCount; // 0xFFになっている桁数
	
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
		carryBuffer = -1;
		carryCount = 0;
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
	// エンコード処理
	public void encodeData(int d) {
		low += range * minBorderList.get(inverseMap.get(d)) / weight;
		range = range * weightList.get(inverseMap.get(d)) / weight;
		// 桁上がりがあるか確認する。
		logger.info(Integer.toHexString(low) + " " + Integer.toHexString(range));
		if(low >= rangeMax) {
			carryBuffer ++;
			if(carryCount != 0) {
				// carryCount = 2のとき
				// 16 FF FFが
				// 17 00 00になります。
				// この場合このタイミングで17 00は書き込んでOK
				target.add((byte)carryBuffer);
				for(int i = 0;i < carryCount - 1; i ++) {
					target.add((byte)0x00);
				}
				carryCount = 0;
				carryBuffer = 0x00;
			}
 			low = low & 0x00FFFFFF;
			logger.info("桁上がり検出");
		}
		if(range < rangeBorder) {
			// 繰り上がり用のbufferが0xffの場合は繰り上げを実施せずに、countを増やしておく。
			range *= 0x0100; // 8bitシフト
			int carryBufferTmp = (low & 0xFF0000) >> 16;
			if(carryBufferTmp == 0xFF) {
				// 追加データが0xFFの場合はcountにいれておきます。
				carryCount ++;
			}
			else {
				if(carryBuffer != -1) {
					target.add((byte)carryBuffer);
				}
				if(carryCount != 0) {
					// carryCountが0でない場合
					// 16 FF FF 15
					target.add((byte)carryBuffer);
					for(int i = 0;i < carryCount;i ++) {
						target.add((byte)0xFF);
					}
					carryCount = 0;
				}
				carryBuffer = carryBufferTmp;
			}
			low = (low & 0x00FFFF) * 0x0100;
		}
	}
	// エンコード結果
	public ByteBuffer getEncodeResult() {
		// のこっているlowをそのまま追加する。
		if(carryBuffer != -1) {
			target.add((byte)carryBuffer);
		}
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
	// デコード処理
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
	}
	/**
	 * デコード処理
	 * @return -1だったらデータなし
	 */
	public Integer decodeData() {
		double dpos = (1f * code * weight / range) + 0.001; // 精度の関係でうまくいかなかったので0.01だけ足しておく。
		int val = 0;
		int pos = (int)dpos;
		for(int i = 0;i < dataList.size();i ++) {
			if(minBorderList.get(i) <= pos && pos < minBorderList.get(i) + weightList.get(i)) {
				val = dataList.get(i);
				code = code - range * minBorderList.get(i) / weight;
				range = range * weightList.get(i) / weight;
				if(range < rangeBorder) {
					if(target.size() <= 0) {
						return -1; // データがなくなったら-1を応答する
					}
					range = range * 0x0100;
					code = code << 8 | (target.remove(0) & 0xFF);
				}
				return val;
			}
		}
		logger.info("おかしなindex値でした。");
		return -1;
	}
}
