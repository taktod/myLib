/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.mkv.test;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

import org.apache.log4j.Logger;

import com.ttProject.container.mkv.MkvBlockTag;
import com.ttProject.container.mkv.MkvTagReader;
import com.ttProject.container.IContainer;
import com.ttProject.frame.IFrame;
import com.ttProject.frame.adpcmimawav.AdpcmImaWavFrame;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IFileReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit16;
import com.ttProject.unit.extra.bit.Bit4;
import com.ttProject.unit.extra.bit.Bit8;

/**
 * test to play adpcm_ima_wav(container is mkv.)
 * @author taktod
 */
public class AdpcmPlayTest3D {
	/** logger */
	private Logger logger = Logger.getLogger(AdpcmPlayTest3D.class);
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
	 * calcurate the index.
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
	 * calcurate next predictor
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
//	@Test
	public void test() throws Exception {
		SourceDataLine audioLine = null;
		int samplingRate = 44100; // 44.1 kHz
		int bit = 16; // 16bit
		AudioFormat format = new AudioFormat((float)samplingRate, bit, 1, true, false);
		// only for monoral here...
		DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
		audioLine = (SourceDataLine)AudioSystem.getLine(info);
		audioLine.open(format);
		audioLine.start();
		
		logger.info("start test");
		// now, try to get data. decode adpcm, play.
		IFileReadChannel source = FileReadChannel.openFileReadChannel(
				"http://49.212.39.17/gc-25-1-3.h264_adpcmimawav5k.mkv"
		);
		IContainer container = null;
		MkvTagReader reader = new MkvTagReader();
		short fourth = 0;
		short third = 0;
		short second = 0;
		while((container = reader.read(source)) != null) {
			if(container instanceof MkvBlockTag) {
				MkvBlockTag blockTag = (MkvBlockTag)container;
				IFrame frame = blockTag.getFrame();
				if(frame instanceof AdpcmImaWavFrame) {
					AdpcmImaWavFrame aFrame = (AdpcmImaWavFrame)frame;
					// frame is available.
					IReadChannel frameData = new ByteReadChannel(aFrame.getData());
					BitLoader loader = new BitLoader(frameData);
					loader.setLittleEndianFlg(true); // treat as little endian.
					Bit16 predictorData = new Bit16();
					Bit8 indexData = new Bit8();
					Bit8 reservedData = new Bit8();
					loader.load(predictorData, indexData, reservedData);
					int predictor = (short)predictorData.get();
					int index = indexData.get();;
					int step = imaStepTable[index];
					// put the data on the buffer.
					ByteBuffer buffer = ByteBuffer.allocate(aFrame.getSampleNum() * 2);
					buffer.order(ByteOrder.LITTLE_ENDIAN);
					buffer.putShort((short)predictor);
					Bit4[] nibbleList = new Bit4[aFrame.getSampleNum() - 1];
					for(int i = 0;i < nibbleList.length;i ++) {
						nibbleList[i] = new Bit4();
					}
					loader.load(nibbleList);
					for(Bit4 nibble : nibbleList) {
						index = nextIndex(index, nibble.get());
						predictor = nextPredictor(index, nibble.get(), predictor, step);
						step = imaStepTable[index];
						buffer.putShort((short)predictor);
					}
					buffer.flip();
					ByteBuffer completeBuffer = ByteBuffer.allocate(buffer.remaining() * 8);
					completeBuffer.order(ByteOrder.LITTLE_ENDIAN);
					while(buffer.remaining() > 0) {
						short first = buffer.getShort();
						double c0, c1, c2, d0, d1, e0;
						c0 = (third - fourth) / 8D;
						c1 = (second - third) / 8D;
						c2 = (first - second) / 8D;
						d0 = (c1 - c0) / 16D;
						d1 = (c2 - c1) / 16D;
						e0 = (d1 - d0) / 24D;
						completeBuffer.putShort((short)(third + (0) * (c0 + (0 - 8) * (d0 + e0 * (0 - 16)))));
						completeBuffer.putShort((short)(third + (1) * (c0 + (1 - 8) * (d0 + e0 * (1 - 16)))));
						completeBuffer.putShort((short)(third + (2) * (c0 + (2 - 8) * (d0 + e0 * (2 - 16)))));
						completeBuffer.putShort((short)(third + (3) * (c0 + (3 - 8) * (d0 + e0 * (3 - 16)))));
						completeBuffer.putShort((short)(third + (4) * (c0 + (4 - 8) * (d0 + e0 * (4 - 16)))));
						completeBuffer.putShort((short)(third + (5) * (c0 + (5 - 8) * (d0 + e0 * (5 - 16)))));
						completeBuffer.putShort((short)(third + (6) * (c0 + (6 - 8) * (d0 + e0 * (6 - 16)))));
						completeBuffer.putShort((short)(third + (7) * (c0 + (7 - 8) * (d0 + e0 * (7 - 16)))));
						fourth = third;
						third = second;
						second = first;
					}
					completeBuffer.flip();
					audioLine.write(completeBuffer.array(), 0, completeBuffer.remaining());
				}
			}
		}
		audioLine.drain();
		audioLine.close();
		audioLine = null;
	}
}
