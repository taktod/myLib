package com.xuggle.test;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.xuggle.xuggler.IAudioSamples;
import com.xuggle.xuggler.IAudioSamples.Format;

/**
 * make beep sound.
 * @author taktod
 */
public class BeepSoundTest {
	/** logger */
	private Logger logger = Logger.getLogger(BeepSoundTest.class);
	/**
	 * @throws Exception
	 */
//	@Test
	public void playTest() throws Exception {
		logger.info("start playTest");
		SourceDataLine audioLine = null;
		IAudioSamples samples = beepSamples();
		
		AudioFormat format = new AudioFormat((float)samples.getSampleRate(), (int)samples.getSampleBitDepth(), samples.getChannels(), true, false);
		DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
		audioLine = (SourceDataLine)AudioSystem.getLine(info);
		audioLine.open(format);
		logger.info("beepstart");
		audioLine.start();
		audioLine.write(samples.getData().getByteArray(0, samples.getSize()), 0, samples.getSize());
		audioLine.drain();
		logger.info("beepend");
		audioLine.close();
		audioLine = null;
	}
	/**
	 * make sine wave xuggler IAudioSamples.
	 * @return
	 */
	private IAudioSamples beepSamples() {
		int sampleRate = 44100; // 44.1KHz
		int sampleNum  = 44100; // 44100 samples(1sec)
		int channel    = 2;     // 2channel(stereo)
		int tone       = 440;   // 440Hz tone.
		int bit        = 16;    // 16bit
		ByteBuffer buffer = ByteBuffer.allocate((int)sampleNum * bit * channel / 8);
		double rad = tone * 2 * Math.PI / sampleRate; // radian for each sample.
		double max = (1 << (bit - 2)) - 1; // ampletude
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		for(int i = 0;i < sampleNum;i ++) {
			short data = (short)(Math.sin(rad * i) * max);
			for(int j = 0;j < channel;j ++) {
				buffer.putShort(data);
			}
		}
		buffer.flip();
		
		IAudioSamples samples = IAudioSamples.make(sampleNum, channel, Format.FMT_S16);
		samples.getData().put(buffer.array(), 0, 0, buffer.remaining());
		samples.setComplete(true, sampleNum, sampleRate, channel, Format.FMT_S16, 0);
		samples.setTimeStamp(0);
		samples.setPts(0);
		return samples;
	}
}
