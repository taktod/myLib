package com.ttProject.container.mkv.test;

import java.nio.ByteBuffer;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ttProject.container.IContainer;
import com.ttProject.container.mkv.CodecType;
import com.ttProject.container.mkv.MkvBlockTag;
import com.ttProject.container.mkv.MkvTag;
import com.ttProject.container.mkv.MkvTagReader;
import com.ttProject.container.mkv.type.TrackEntry;
import com.ttProject.container.mkv.type.Tracks;
import com.ttProject.frame.IFrame;
import com.ttProject.frame.adpcmimawav.AdpcmImaWavFrame;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit16;
import com.ttProject.unit.extra.bit.Bit4;
import com.ttProject.unit.extra.bit.Bit8;
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
		AdpcmDecoder decoder = null;
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
				if(container instanceof Tracks) {
					for(MkvTag tag : ((Tracks) container).getChildList()) {
						if(tag instanceof TrackEntry) {
							TrackEntry entry = (TrackEntry) tag;
							if(entry.getCodecType() == CodecType.A_MS_ACM) {
								// adpcm_ima_wavである
								decoder = new AdpcmDecoder((int)entry.getSampleRate(), entry.getChannels());
							}
						}
					}
				}
				if(container instanceof MkvBlockTag) {
					MkvBlockTag blockTag = (MkvBlockTag) container;
//					logger.info(blockTag);
					IFrame frame = blockTag.getFrame();
					if(frame instanceof AdpcmImaWavFrame) {
						AdpcmImaWavFrame aFrame = (AdpcmImaWavFrame)frame;
						// データを参照します。
						ByteBuffer buffer = frame.getData();
						logger.info(HexUtil.toHex(buffer));
						List<Integer> data = decoder.getDecodedData(buffer);
/*						for(int dat : data) {
							logger.info(dat);
						}*/
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
		/** チャンネル数 */
		private final int channels;
		/** サンプルレート */
		private final int sampleRate;
		/**
		 * コンストラクタ
		 * @param sampleRate
		 * @param channels
		 */
		public AdpcmDecoder(int sampleRate, int channels) {
			this.sampleRate = sampleRate;
			this.channels = channels;
			if(channels != 1 && channels != 2) {
				throw new RuntimeException("モノラルとステレオ以外は処理しません");
			}
			logger.info(sampleRate + " / " + channels);
		}
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
		public List<Integer> getDecodedData(ByteBuffer data) throws Exception {
			IReadChannel readChannel = new ByteReadChannel(data);
			// 始めの16bitが初期のpredictor
			// 次の8bitがleftIndex
			Bit16 leftPredictor = new Bit16();
			int lpredictor = leftPredictor.get();
			Bit8  leftIndex     = new Bit8();
			int lindex = leftIndex.get();
			Bit8  leftReserved  = new Bit8();
			int lstep = imaStepTable[lindex];
			Bit16 rightPredictor = null;
			int rpredictor = 0;
			Bit8  rightIndex     = null;
			int rindex = 0;
			Bit8  rightReserved  = null;
			int rstep = 0;
			BitLoader loader = new BitLoader(readChannel);
			loader.load(leftPredictor, leftIndex, leftReserved);
			if(channels == 2) {
				rightPredictor = new Bit16();
				rightIndex     = new Bit8();
				rightReserved  = new Bit8();
				loader.load(rightPredictor, rightIndex, rightReserved);
				rpredictor = rightPredictor.get();
				rindex = rightIndex.get();
				rstep = imaStepTable[rindex];
			}
			do {
				// 8つ4bitが続く
				// left側
				Bit4[] bit4List = new Bit4[8];
				for(int i = 0;i < bit4List.length;i ++) {
					bit4List[i] = new Bit4();
				}
				loader.load(bit4List);
				for(Bit4 nibble : bit4List) {
					lindex = nextIndex(lindex, nibble.get());
					lpredictor = nextPredictor(lindex, nibble.get(), lpredictor, lstep);
					lstep = imaStepTable[lindex];
				}
				if(channels == 2) {
					// right側
					for(int i = 0;i < bit4List.length;i ++) {
						bit4List[i] = new Bit4();
					}
					loader.load(bit4List);
					for(Bit4 nibble : bit4List) {
						rindex = nextIndex(rindex, nibble.get());
						rpredictor = nextPredictor(rindex, nibble.get(), rpredictor, rstep);
						rstep = imaStepTable[rindex];
						logger.info(rpredictor);
					}
				}
			} while(readChannel.position() < readChannel.size());
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
