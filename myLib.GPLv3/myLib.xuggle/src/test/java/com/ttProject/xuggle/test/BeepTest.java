/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 */
package com.ttProject.xuggle.test;

import java.nio.ByteBuffer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

/**
 * 音の波形のテストをやってみる
 * @author taktod
 */
public class BeepTest {
//	@Test
	public void test() throws Exception {
		SourceDataLine audioLine = null;
		int samplingRate = 44100; // 8kHz
		int length = 1; // 音の長さ
		int tone = 440; // 440hzのtone
		int bit = 16; // 8bit
		int tone2 = 400; // 660hzのtone
//		short[] data = new short[samplingRate * length];
		ByteBuffer buffer = ByteBuffer.allocate((int)(samplingRate * length * bit * 2 / 8));
		double rad = tone * 2 * Math.PI / samplingRate;
		double rad2 = tone2 * 2 * Math.PI / samplingRate;

		// 振幅の最大値
		double max = (1 << (bit - 2)) - 1;
		for(int i = 0;i < samplingRate * length;i ++) {
			short data = (short)((Math.sin(rad * i) * max) + (Math.sin(rad2 * i) * max));
			buffer.putShort(data);
			buffer.putShort(data);
		}
		buffer.flip();
		AudioFormat format = new AudioFormat((float)samplingRate,
				bit,
				2,
				true,
				true);
		DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
		audioLine = (SourceDataLine)AudioSystem.getLine(info);
		audioLine.open(format);
		audioLine.start();
		audioLine.write(buffer.array(), 0, buffer.remaining());
		audioLine.drain();
		audioLine.close();
		audioLine = null;
//		Clip clip = AudioSystem.getClip();
//		clip.open(format, buffer.array(), 0, buffer.remaining());
		
//		clip.start();
//		clip.drain();
//		clip.close();
	}
}
