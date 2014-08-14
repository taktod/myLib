package com.ttProject.container.mkv.test;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ttProject.container.IContainer;
import com.ttProject.container.mkv.MkvBlockTag;
import com.ttProject.container.mkv.MkvTagReader;
import com.ttProject.frame.IFrame;
import com.ttProject.frame.adpcmimawav.AdpcmImaWavFrame;
import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.util.HexUtil;

/**
 * 高速フーリエ変換の動作テスト用のテスト
 * @author taktod
 */
public class FftTest {
	/** ロガー */
	private Logger logger = Logger.getLogger(FftTest.class);
	/**
	 * テスト
	 */
	@Test
	public void test() {
		logger.info("テスト開始");
		// とりあえず適当なデータを調整することにします。
		// データは２の累乗でないとだめっぽいです。
		IReadChannel source = null;
		try {
			source = FileReadChannel.openFileReadChannel(
					Thread.currentThread().getContextClassLoader().getResource("test.mjpegadpcmimawav.mkv")
			);
			MkvTagReader reader = new MkvTagReader();
			IContainer container = null;
			while((container = reader.read(source)) != null) {
				if(container instanceof MkvBlockTag) {
					MkvBlockTag blockTag = (MkvBlockTag) container;
//					logger.info(blockTag);
					IFrame frame = blockTag.getFrame();
					if(frame instanceof AdpcmImaWavFrame) {
						AdpcmImaWavFrame aFrame = (AdpcmImaWavFrame)frame;
						logger.info(aFrame.getBit());
						// データを参照します。
						ByteBuffer buffer = frame.getData();
						logger.info(HexUtil.toHex(buffer));
						// はじめの4byteを落としてから元のデータにデコードするプログラムを書いておく。
						break;
					}
				}
			}
		}
		catch(Exception e) {
			logger.error("例外が発生しました。", e);
		}
	}
	/**
	 * adpcmをデコードするクラス
	 * @author taktod
	 */
	public static class AdpcmDecoder {
		/** ロガー */
		private Logger logger = Logger.getLogger(AdpcmDecoder.class);
		private int imaIndexTable[] = {
			-1, -1, -1, -1, 2, 4, 6, 8,
			-1, -1, -1, -1, 2, 4, 6, 8
		};
		private int imaStepTable[] = {
			7, 8, 9, 10, 11, 12, 13, 14, 16, 17,
			19, 21, 23, 25, 28, 31, 34, 37, 41, 45,
			50, 55, 60, 66, 73, 80, 88, 97, 107, 118,
			130, 143, 157, 173, 190, 209, 230, 253, 279, 307,
			337, 371, 408, 449, 494, 544, 598, 658, 724, 796,
			876, 963, 1060, 1166, 1282, 1411, 1552, 1707, 1878, 2066,
			2272, 2499, 2749, 3024, 3327, 3660, 4026, 4428, 4871, 5358,
			5894, 6484, 7132, 7845, 8630, 9493, 10442, 11487, 12635, 13899,
			15289, 16818, 18500, 20350, 22385, 24623, 27086, 29794, 32767
		};
		/**
		 * 次のindex値を計算します。
		 * @param index
		 * @param nibble
		 * @return
		 */
		private int nextIndex(int index, int nibble) {
			int newIndex = index + imaIndexTable[nibble];
			if(newIndex < 0) {
				return 0;
			}
			else if(newIndex > 88) {
				return 88;
			}
			else {
				return newIndex;
			}
		}
		/**
		 * 次の振幅を計算します。
		 */
		private int nextPredictor(int index, int nibble, int predictor, int step) throws Exception {
			boolean sign = (nibble & 0x08) == 0x08;
			int delta = nibble & 0x07;
			int diff = step >> 1;
			if((delta & 0x04) == 4) {
				diff += (step << 2);
			}
			if((delta & 0x02) == 2) {
				diff += (step << 1);
			}
			if((delta & 0x01) == 1) {
				diff += step;
			}
			diff >>= 2;
			if(sign) {
				predictor -= diff;
			}
			else {
				predictor += diff;
			}
			if(predictor > 32767) {
				return 32767;
			}
			else if(predictor < -32768) {
				return -32768;
			}
			else {
				return predictor;
			}
		}
		/**
		 * デコード後のデータを取得する
		 * @return
		 */
		public int[] getDecodedData() {
			return null;
		}
	}
	/**
	 * fft用のクラス
	 * @author taktod
	 */
	public static class Fft {
		
	}
}
