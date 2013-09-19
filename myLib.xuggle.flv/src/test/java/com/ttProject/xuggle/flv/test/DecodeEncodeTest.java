package com.ttProject.xuggle.flv.test;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.sound.sampled.AudioFormat;

import org.junit.Test;

import com.ttProject.media.flv.Tag;
import com.ttProject.media.raw.AudioData;
import com.ttProject.xuggle.flv.FlvDepacketizer;
import com.ttProject.xuggle.flv.FlvPacketizer;
import com.ttProject.xuggle.raw.AudioConverter;
import com.xuggle.xuggler.IAudioSamples;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.IStreamCoder.Direction;

/**
 * エンコードとデコードを複合させるテスト
 * @author taktod
 */
public class DecodeEncodeTest {
	private int audioCounter = 0;
	/**
	 * ラの音のaudioデータをつくって応答する。
	 * @return
	 */
	public AudioData audioData() {
		// とりあえずラの音で1024サンプル数つくることにする。
		int samplingRate = 44100;
		int tone = 440;
		int bit = 16;
		int channels = 2;
		int samplesNum = 1024;
		// 1024サンプル + 16bit + 2channels / (byte化)
		ByteBuffer buffer = ByteBuffer.allocate((int)samplesNum * bit * channels / 8);
		double rad = tone * 2 * Math.PI / samplingRate; // 各deltaごとの回転数
		double max = (1 << (bit - 2)) - 1; // 振幅の大きさ(音の大きさ)
		buffer.order(ByteOrder.LITTLE_ENDIAN); // xuggleで利用するデータはlittleEndianなのでlittleEndianを使うようにする。
		for(int i = 0;i < samplesNum / 8;i ++, audioCounter ++) {
			short data = (short)(Math.sin(rad * audioCounter) * max);
			for(int j = 0;j < channels;j ++) {
				buffer.putShort(data);
			}
		}
		buffer.flip();
		return new AudioData(new AudioFormat(44100, bit, channels, true, false), buffer);
	}
	@Test
	public void test() {
		audioCounter = 0;
		AudioConverter converter = new AudioConverter();
		try {
			IStreamCoder encoder1 = IStreamCoder.make(Direction.ENCODING, ICodec.ID.CODEC_ID_MP3);
			encoder1.setSampleRate(44100);
			encoder1.setChannels(2);
			encoder1.setBitRate(96000);
			if(encoder1.open(null, null) < 0) {
				throw new Exception("変換コーダーが開けませんでした。");
			}
			int index = 0;
			FlvDepacketizer depacketizer = new FlvDepacketizer();
			FlvPacketizer packetizer = new FlvPacketizer();
			IStreamCoder decoder = null;
			while(index < 2000) {
				index ++;
				AudioData audioData = audioData();
				IAudioSamples samples = converter.makeSamples(audioData);
				IPacket packet = IPacket.make();
				int samplesConsumed = 0;
				while(samplesConsumed < samples.getNumSamples()) {
					int retval = encoder1.encodeAudio(packet, samples, samplesConsumed);
					if(retval < 0) {
						throw new Exception("変換失敗１");
					}
					samplesConsumed += retval;
					// ここまででpacketができている。
					if(packet.isComplete()) {
						for(Tag tag : depacketizer.getTag(encoder1, packet)) {
							System.out.println(tag);
							// ここからこのデータをデコードする
							IPacket pkt = packetizer.getPacket(tag);
							if(pkt == null) {
								continue;
							}
							if(decoder == null) {
								decoder = packetizer.createAudioDecoder();
							}
							IAudioSamples as = IAudioSamples.make(1024, decoder.getChannels());
							int offset = 0;
							while(offset < pkt.getSize()) {
								int bytesDecoded = decoder.decodeAudio(as, pkt, offset);
								if(bytesDecoded < 0) {
									throw new Exception("変換失敗２");
								}
								offset += bytesDecoded;
								if(samples.isComplete()) {
									System.out.println(samples);
								}
							}
						}
					}
					// mp3ができあがっているはずなので、デコードに回す
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
