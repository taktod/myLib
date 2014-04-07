package com.ttProject.frame.adpcmimawav.test;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IFileReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit16;
import com.ttProject.unit.extra.bit.Bit4;
import com.ttProject.unit.extra.bit.Bit8;
import com.ttProject.util.BufferUtil;
import com.ttProject.util.HexUtil;

/**
 * adpcm_ima_wavのフォーマットがどうなっているかよくわからないので、AudioLineで再生してみたいと思います。
 * @see http://vavi-sound.googlecode.com/svn/trunk/vavi-sound/src/test/java/vavi/sound/adpcm/dvi/Adpcm.java
 * @see http://wiki.multimedia.cx/index.php?title=IMA_ADPCM
 * 参考にしてみたurl
 * @author taktod
 */
public class SoundTest {
	/** ロガー */
	private Logger logger = Logger.getLogger(SoundTest.class);
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
	private int nextPredictor(int index, int nibble, int predictor, int step) {
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
	@Test
	public void test() throws Exception {
		SourceDataLine audioLine = null;
		int samplingRate = 44100; // 44.1 kHz
		int bit = 16; // 16bit
		AudioFormat format = new AudioFormat((float)samplingRate, bit, 2, true, true);
		DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
//		audioLine
		IFileReadChannel channel = FileReadChannel.openFileReadChannel(
				Thread.currentThread().getContextClassLoader().getResource("test_mono.wav")
		);
		logger.info("テスト");
		channel.position(0x5c);
		// データを読み込んでいきます。
		Bit16 leftPredictor = new Bit16();
		Bit8  leftIndex = new Bit8();
		Bit8  reservedLeft = new Bit8();
		Bit16 rightPredictor = new Bit16();
		Bit8  rightIndex = new Bit8();
		Bit8  reservedRight = new Bit8();
		BitLoader loader = new BitLoader(channel);
		loader.setLittleEndianFlg(true);
		loader.load(leftPredictor, leftIndex, reservedLeft,
					rightPredictor,rightIndex,reservedRight);
		int lpredictor = leftPredictor.get();
		int lindex = leftIndex.get();
		int lstep = imaStepTable[lindex];
		int rpredictor = rightPredictor.get();
		int rindex = rightIndex.get();
		int rstep = imaStepTable[rindex];
		// ここから先は4byteごとに読み込む必要あり。
		ByteBuffer buffer = ByteBuffer.allocate(800);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		for(int a = 0;a < 40;a ++) {
			// とりあえずleft側
			Bit4[] bit4List = new Bit4[8];
			for(int i = 0;i < bit4List.length;i ++) {
				bit4List[i] = new Bit4();
			}
			loader.load(bit4List);
			// nextprevを取得してからnextIndexを取得する・・・ってか
//			logger.info("left");
			for(Bit4 nibble : bit4List) {
				lindex = nextIndex(lindex, nibble.get());
				lpredictor = nextPredictor(lindex, nibble.get(), lpredictor, lstep);
				lstep = imaStepTable[lindex];
				logger.info(lpredictor);
				buffer.putShort((short)lpredictor);
			}
			// つづいてright側
/*			bit4List = new Bit4[8];
			for(int i = 0;i < bit4List.length;i ++) {
				bit4List[i] = new Bit4();
			}
			loader.load(bit4List);
			// nextprevを取得してからnextIndexを取得する・・・ってか
			logger.info("right");
			for(Bit4 nibble : bit4List) {
				rindex = nextIndex(rindex, nibble.get());
				rpredictor = nextPredictor(rindex, nibble.get(), rpredictor, rstep);
				rstep = imaStepTable[rindex];
				logger.info(rpredictor);
			}*/
		}
		buffer.flip();
		logger.info(HexUtil.toHex(buffer, true));
	}
//	@Test
	public void test2() throws Exception {
		IReadChannel channel = new ByteReadChannel(HexUtil.makeBuffer("0123456789"));
		Bit4[] bit4List = new Bit4[8];
		for(int i = 0;i < bit4List.length;i ++) {
			bit4List[i] = new Bit4();
		}
		BitLoader loader = new BitLoader(channel);
		loader.setLittleEndianFlg(true);
		loader.load(bit4List);
		for(Bit4 b : bit4List) {
			logger.info(b.get());
		}
	}
}
