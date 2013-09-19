package com.ttProject.xuggle.flv.test;

import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.concurrent.LinkedBlockingQueue;

import javax.sound.sampled.AudioFormat;

import org.junit.Test;

import com.ttProject.media.flv.FlvHeader;
import com.ttProject.media.flv.Tag;
import com.ttProject.media.raw.AudioData;
import com.ttProject.xuggle.flv.FlvDepacketizer;
import com.ttProject.xuggle.raw.AudioConverter;
import com.xuggle.xuggler.IAudioSamples;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.IStreamCoder.Direction;

/**
 * 複数のxuggleを同時に走らせるとどうなるかというテスト
 * @author taktod
 */
public class MultiCoderTest {
	private int audioCounter = 0;
	private int audioCounter2 = 0;
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
	/**
	 * ラの音のaudioデータをつくって応答する。
	 * @return
	 */
	public AudioData audioData2() {
		// とりあえずラの音で1024サンプル数つくることにする。
		int samplingRate = 44100;
		int tone = 340;
		int bit = 16;
		int channels = 2;
		int samplesNum = 1024;
		// 1024サンプル + 16bit + 2channels / (byte化)
		ByteBuffer buffer = ByteBuffer.allocate((int)samplesNum * bit * channels / 8);
		double rad = tone * 2 * Math.PI / samplingRate; // 各deltaごとの回転数
		double max = (1 << (bit - 2)) - 1; // 振幅の大きさ(音の大きさ)
		buffer.order(ByteOrder.LITTLE_ENDIAN); // xuggleで利用するデータはlittleEndianなのでlittleEndianを使うようにする。
		for(int i = 0;i < samplesNum / 8;i ++, audioCounter2 ++) {
			short data = (short)(Math.sin(rad * audioCounter2) * max);
			for(int j = 0;j < channels;j ++) {
				buffer.putShort(data);
			}
		}
		buffer.flip();
		return new AudioData(new AudioFormat(44100, bit, channels, true, false), buffer);
	}
	/**
	 * 複数の変換を同時に走らせる動作
	 * ただしシングルスレッド
	 * この動作からすると、IAudioSamplesの使いまわしは可能っぽいですね。
	 * @throws Exception
	 */
	@Test
	public void aacmp3Test() throws Exception {
		audioCounter = 0;
		FileChannel mp3stereo =null;
		FileChannel aacstereo =null;
		AudioConverter converter = new AudioConverter();
		IStreamCoder mp3stereoEncoder = null;
		IStreamCoder aacstereoEncoder = null;
		try {
			mp3stereo = new FileOutputStream("mp3stereo.flv").getChannel();
			aacstereo = new FileOutputStream("aacstereo.flv").getChannel();
			FlvHeader flvHeader = new FlvHeader();
			flvHeader.setVideoFlg(false);
			flvHeader.setAudioFlg(true);
			mp3stereo.write(flvHeader.getBuffer());
			aacstereo.write(flvHeader.getBuffer());
			
			mp3stereoEncoder = IStreamCoder.make(Direction.ENCODING, ICodec.ID.CODEC_ID_MP3);
			mp3stereoEncoder.setSampleRate(44100);
			mp3stereoEncoder.setChannels(2);
			mp3stereoEncoder.setBitRate(96000);
			if(mp3stereoEncoder.open(null, null) < 0) {
				throw new Exception("mp3stereo開けませんでした");
			}
			aacstereoEncoder = IStreamCoder.make(Direction.ENCODING, ICodec.ID.CODEC_ID_AAC);
			aacstereoEncoder.setSampleRate(44100);
			aacstereoEncoder.setChannels(2);
			aacstereoEncoder.setBitRate(96000);
			if(aacstereoEncoder.open(null, null) < 0) {
				throw new Exception("aacstereo開けませんでした");
			}
			int index = 0;
			FlvDepacketizer mp3depacketizer = new FlvDepacketizer();
			FlvDepacketizer aacdepacketizer = new FlvDepacketizer();
			while(index < 2000) {
				index ++;
				AudioData audioData = audioData();
				IAudioSamples samples = converter.makeSamples(audioData);
				IPacket packet = IPacket.make();
				int samplesConsumed1 = 0;
				while(samplesConsumed1 < samples.getNumSamples()) {
					int retval = mp3stereoEncoder.encodeAudio(packet, samples, samplesConsumed1);
					if(retval < 0) {
						throw new Exception("変換失敗1");
					}
					samplesConsumed1 += retval;
					if(packet.isComplete()) {
						for(Tag tag : mp3depacketizer.getTag(mp3stereoEncoder, packet)) {
							mp3stereo.write(tag.getBuffer());
						}
					}
				}
				int samplesConsumed2 = 0;
				while(samplesConsumed2 < samples.getNumSamples()) {
					int retval = aacstereoEncoder.encodeAudio(packet, samples, samplesConsumed2);
					if(retval < 0) {
						throw new Exception("変換失敗2");
					}
					samplesConsumed2 += retval;
					if(packet.isComplete()) {
						for(Tag tag : aacdepacketizer.getTag(aacstereoEncoder, packet)) {
							aacstereo.write(tag.getBuffer());
						}
					}
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally{
			if(mp3stereoEncoder != null) {
				mp3stereoEncoder.close();
				mp3stereoEncoder = null;
			}
			if(aacstereoEncoder != null) {
				aacstereoEncoder.close();
				aacstereoEncoder = null;
			}
			if(mp3stereo != null) {
				try {
					mp3stereo.close();
				}
				catch (Exception e) {
				}
				mp3stereo = null;
			}
			if(aacstereo != null) {
				try {
					aacstereo.close();
				}
				catch (Exception e) {
				}
				aacstereo = null;
			}
		}
	}
	/**
	 * マルチスレッドで変換させる動作で1ソース複数同時コンバートを実行してみます。
	 * 速度かわらず。
	 */
	boolean workingFlg = true;
	@Test
	public void aacmp3MultiThreadTest() {
		audioCounter = 0;
		workingFlg = true;
		FileChannel mp3stereo = null;
		FileChannel aacstereo = null;
		AudioConverter converter = new AudioConverter();
		try {
			final LinkedBlockingQueue<IAudioSamples> aacSamples = new LinkedBlockingQueue<IAudioSamples>();
			final LinkedBlockingQueue<IAudioSamples> mp3Samples = new LinkedBlockingQueue<IAudioSamples>();
			mp3stereo = new FileOutputStream("mp3stereo2.flv").getChannel();
			aacstereo = new FileOutputStream("aacstereo2.flv").getChannel();
			FlvHeader flvHeader = new FlvHeader();
			flvHeader.setVideoFlg(false);
			flvHeader.setAudioFlg(true);
			mp3stereo.write(flvHeader.getBuffer());
			aacstereo.write(flvHeader.getBuffer());

			final FileChannel mp3Channel = mp3stereo;
			final FileChannel aacChannel = aacstereo;
			Thread mp3Thread = new Thread(new Runnable() {
				@Override
				public void run() {
					IStreamCoder mp3stereoEncoder = null;
					try {
						mp3stereoEncoder = IStreamCoder.make(Direction.ENCODING, ICodec.ID.CODEC_ID_MP3);
						mp3stereoEncoder.setSampleRate(44100);
						mp3stereoEncoder.setChannels(2);
						mp3stereoEncoder.setBitRate(96000);
						if(mp3stereoEncoder.open(null, null) < 0) {
							throw new Exception("mp3stereo開けませんでした");
						}
						while(workingFlg || mp3Samples.size() > 0) {
							IAudioSamples samples = mp3Samples.take();
							IPacket packet = IPacket.make();
							FlvDepacketizer mp3depacketizer = new FlvDepacketizer();
							int samplesConsumed = 0;
							while(samplesConsumed < samples.getNumSamples()) {
								// TODO この部分はsynchronizedをはずしても同じ結果でした。xuggleの内部でlockかかっているのかな・・・
//								synchronized(samples) {
									int retval = mp3stereoEncoder.encodeAudio(packet, samples, samplesConsumed);
									if(retval < 0) {
										throw new Exception("変換失敗");
									}
									samplesConsumed += retval;
//								}
								if(packet.isComplete()) {
									for(Tag tag : mp3depacketizer.getTag(mp3stereoEncoder, packet)) {
										mp3Channel.write(tag.getBuffer());
									}
								}
							}
						}
					}
					catch (Exception e) {
						e.printStackTrace();
					}
					finally {
						if(mp3stereoEncoder != null) {
							mp3stereoEncoder.close();
							mp3stereoEncoder = null;
						}
					}
				}
			});
			mp3Thread.setName("mp3ConvertThread");
			mp3Thread.start();
			Thread aacThread = new Thread(new Runnable() {
				@Override
				public void run() {
					IStreamCoder aacstereoEncoder = null;
					try {
						aacstereoEncoder = IStreamCoder.make(Direction.ENCODING, ICodec.ID.CODEC_ID_AAC);
						aacstereoEncoder.setSampleRate(44100);
						aacstereoEncoder.setChannels(2);
						aacstereoEncoder.setBitRate(96000);
						if(aacstereoEncoder.open(null, null) < 0) {
							throw new Exception("aacstereo開けませんでした");
						}
						while(workingFlg || aacSamples.size() > 0) {
							IAudioSamples samples = aacSamples.take();
							IPacket packet = IPacket.make();
							FlvDepacketizer aacdepacketizer = new FlvDepacketizer();
							int samplesConsumed = 0;
							while(samplesConsumed < samples.getNumSamples()) {
//								synchronized(samples) {
									int retval = aacstereoEncoder.encodeAudio(packet, samples, samplesConsumed);
									if(retval < 0) {
										throw new Exception("変換失敗");
									}
									samplesConsumed += retval;
//								}
								if(packet.isComplete()) {
									for(Tag tag : aacdepacketizer.getTag(aacstereoEncoder, packet)) {
										aacChannel.write(tag.getBuffer());
									}
								}
							}
						}
					}
					catch (Exception e) {
						e.printStackTrace();
					}
					finally {
						if(aacstereoEncoder != null) {
							aacstereoEncoder.close();
							aacstereoEncoder = null;
						}
					}
				}
			}); 
			aacThread.setName("aacConvertThread");
			aacThread.start();
			int index = 0;
			while(index < 2000) {
				index ++;
				AudioData audioData = audioData();
				IAudioSamples samples = converter.makeSamples(audioData);
				// これをqueueにいれておく。
				aacSamples.add(samples);
				mp3Samples.add(samples);
			}
			workingFlg = false;
			// このタイミングでデータをいれていく。
			mp3Thread.join();
			aacThread.join();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally{
			if(mp3stereo != null) {
				try {
					mp3stereo.close();
				}
				catch (Exception e) {
				}
				mp3stereo = null;
			}
			if(aacstereo != null) {
				try {
					aacstereo.close();
				}
				catch (Exception e) {
				}
				aacstereo = null;
			}
		}
	}
	/**
	 * audioDataのソースを別のデータに変更してみることにします。
	 * これでaudioSamples同士が一致しないので、lockしなくなって早くなるかも？
	 * 早くなりました。
	 * ソースを別のものとしてしまえばどうということない・・・ということでしょうかね。
	 */
	@Test
	public void aacmp3MultiThreadTest2() {
		audioCounter = 0;
		workingFlg = true;
		FileChannel mp3stereo = null;
		FileChannel aacstereo = null;
		final AudioConverter converter = new AudioConverter();
		try {
			mp3stereo = new FileOutputStream("mp3stereo3.flv").getChannel();
			aacstereo = new FileOutputStream("aacstereo3.flv").getChannel();
			FlvHeader flvHeader = new FlvHeader();
			flvHeader.setVideoFlg(false);
			flvHeader.setAudioFlg(true);
			mp3stereo.write(flvHeader.getBuffer());
			aacstereo.write(flvHeader.getBuffer());

			final FileChannel mp3Channel = mp3stereo;
			final FileChannel aacChannel = aacstereo;
			Thread mp3Thread = new Thread(new Runnable() {
				@Override
				public void run() {
					IStreamCoder mp3stereoEncoder = null;
					try {
						mp3stereoEncoder = IStreamCoder.make(Direction.ENCODING, ICodec.ID.CODEC_ID_MP3);
						mp3stereoEncoder.setSampleRate(44100);
						mp3stereoEncoder.setChannels(2);
						mp3stereoEncoder.setBitRate(96000);
						if(mp3stereoEncoder.open(null, null) < 0) {
							throw new Exception("mp3stereo開けませんでした");
						}
						int index = 0;
						while(index < 2000) {
							index ++;
							AudioData audioData = audioData2();
							IAudioSamples samples = converter.makeSamples(audioData);
							IPacket packet = IPacket.make();
							FlvDepacketizer mp3depacketizer = new FlvDepacketizer();
							int samplesConsumed = 0;
							while(samplesConsumed < samples.getNumSamples()) {
								// TODO この部分はsynchronizedをはずしても同じ結果でした。xuggleの内部でlockかかっているのかな・・・
								int retval = mp3stereoEncoder.encodeAudio(packet, samples, samplesConsumed);
								if(retval < 0) {
									throw new Exception("変換失敗");
								}
								samplesConsumed += retval;
								if(packet.isComplete()) {
									for(Tag tag : mp3depacketizer.getTag(mp3stereoEncoder, packet)) {
										mp3Channel.write(tag.getBuffer());
									}
								}
							}
						}
					}
					catch (Exception e) {
						e.printStackTrace();
					}
					finally {
						if(mp3stereoEncoder != null) {
							mp3stereoEncoder.close();
							mp3stereoEncoder = null;
						}
					}
				}
			});
			mp3Thread.setName("mp3ConvertThread");
			mp3Thread.start();
			Thread aacThread = new Thread(new Runnable() {
				@Override
				public void run() {
					IStreamCoder aacstereoEncoder = null;
					try {
						aacstereoEncoder = IStreamCoder.make(Direction.ENCODING, ICodec.ID.CODEC_ID_AAC);
						aacstereoEncoder.setSampleRate(44100);
						aacstereoEncoder.setChannels(2);
						aacstereoEncoder.setBitRate(96000);
						if(aacstereoEncoder.open(null, null) < 0) {
							throw new Exception("aacstereo開けませんでした");
						}
						int index = 0;
						while(index < 2000) {
							index ++;
							AudioData audioData = audioData();
							IAudioSamples samples = converter.makeSamples(audioData);
							IPacket packet = IPacket.make();
							FlvDepacketizer aacdepacketizer = new FlvDepacketizer();
							int samplesConsumed = 0;
							while(samplesConsumed < samples.getNumSamples()) {
								int retval = aacstereoEncoder.encodeAudio(packet, samples, samplesConsumed);
								if(retval < 0) {
									throw new Exception("変換失敗");
								}
								samplesConsumed += retval;
								if(packet.isComplete()) {
									for(Tag tag : aacdepacketizer.getTag(aacstereoEncoder, packet)) {
										aacChannel.write(tag.getBuffer());
									}
								}
							}
						}
					}
					catch (Exception e) {
						e.printStackTrace();
					}
					finally {
						if(aacstereoEncoder != null) {
							aacstereoEncoder.close();
							aacstereoEncoder = null;
						}
					}
				}
			}); 
			aacThread.setName("aacConvertThread");
			aacThread.start();
			// このタイミングでデータをいれていく。
			mp3Thread.join();
			aacThread.join();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally{
			if(mp3stereo != null) {
				try {
					mp3stereo.close();
				}
				catch (Exception e) {
				}
				mp3stereo = null;
			}
			if(aacstereo != null) {
				try {
					aacstereo.close();
				}
				catch (Exception e) {
				}
				aacstereo = null;
			}
		}
	}
	/**
	 * audioDataは１つにして、そこからIAudioSampleをつくるようにした場合も高速になった。
	 * 変換そのものを並列タスクで動作させること自体は問題ないっぽいですね。
	 * これでthreadがうまく使えそう・・・
	 */
	@Test
	public void aacmp3MultiThreadTest3() {
		audioCounter = 0;
		workingFlg = true;
		FileChannel mp3stereo = null;
		FileChannel aacstereo = null;
		final AudioConverter converter = new AudioConverter();
		try {
			final LinkedBlockingQueue<AudioData> aacData = new LinkedBlockingQueue<AudioData>();
			final LinkedBlockingQueue<AudioData> mp3Data = new LinkedBlockingQueue<AudioData>();
			mp3stereo = new FileOutputStream("mp3stereo4.flv").getChannel();
			aacstereo = new FileOutputStream("aacstereo4.flv").getChannel();
			FlvHeader flvHeader = new FlvHeader();
			flvHeader.setVideoFlg(false);
			flvHeader.setAudioFlg(true);
			mp3stereo.write(flvHeader.getBuffer());
			aacstereo.write(flvHeader.getBuffer());

			final FileChannel mp3Channel = mp3stereo;
			final FileChannel aacChannel = aacstereo;
			Thread mp3Thread = new Thread(new Runnable() {
				@Override
				public void run() {
					IStreamCoder mp3stereoEncoder = null;
					try {
						mp3stereoEncoder = IStreamCoder.make(Direction.ENCODING, ICodec.ID.CODEC_ID_MP3);
						mp3stereoEncoder.setSampleRate(44100);
						mp3stereoEncoder.setChannels(2);
						mp3stereoEncoder.setBitRate(96000);
						if(mp3stereoEncoder.open(null, null) < 0) {
							throw new Exception("mp3stereo開けませんでした");
						}
						while(workingFlg || mp3Data.size() > 0) {
							IAudioSamples samples = converter.makeSamples(mp3Data.take());
							IPacket packet = IPacket.make();
							FlvDepacketizer mp3depacketizer = new FlvDepacketizer();
							int samplesConsumed = 0;
							while(samplesConsumed < samples.getNumSamples()) {
								// TODO この部分はsynchronizedをはずしても同じ結果でした。xuggleの内部でlockかかっているのかな・・・
//								synchronized(samples) {
									int retval = mp3stereoEncoder.encodeAudio(packet, samples, samplesConsumed);
									if(retval < 0) {
										throw new Exception("変換失敗");
									}
									samplesConsumed += retval;
//								}
								if(packet.isComplete()) {
									for(Tag tag : mp3depacketizer.getTag(mp3stereoEncoder, packet)) {
										mp3Channel.write(tag.getBuffer());
									}
								}
							}
						}
					}
					catch (Exception e) {
						e.printStackTrace();
					}
					finally {
						if(mp3stereoEncoder != null) {
							mp3stereoEncoder.close();
							mp3stereoEncoder = null;
						}
					}
				}
			});
			mp3Thread.setName("mp3ConvertThread");
			mp3Thread.start();
			Thread aacThread = new Thread(new Runnable() {
				@Override
				public void run() {
					IStreamCoder aacstereoEncoder = null;
					try {
						aacstereoEncoder = IStreamCoder.make(Direction.ENCODING, ICodec.ID.CODEC_ID_AAC);
						aacstereoEncoder.setSampleRate(44100);
						aacstereoEncoder.setChannels(2);
						aacstereoEncoder.setBitRate(96000);
						if(aacstereoEncoder.open(null, null) < 0) {
							throw new Exception("aacstereo開けませんでした");
						}
						while(workingFlg || aacData.size() > 0) {
							IAudioSamples samples = converter.makeSamples(aacData.take());
							IPacket packet = IPacket.make();
							FlvDepacketizer aacdepacketizer = new FlvDepacketizer();
							int samplesConsumed = 0;
							while(samplesConsumed < samples.getNumSamples()) {
//								synchronized(samples) {
									int retval = aacstereoEncoder.encodeAudio(packet, samples, samplesConsumed);
									if(retval < 0) {
										throw new Exception("変換失敗");
									}
									samplesConsumed += retval;
//								}
								if(packet.isComplete()) {
									for(Tag tag : aacdepacketizer.getTag(aacstereoEncoder, packet)) {
										aacChannel.write(tag.getBuffer());
									}
								}
							}
						}
					}
					catch (Exception e) {
						e.printStackTrace();
					}
					finally {
						if(aacstereoEncoder != null) {
							aacstereoEncoder.close();
							aacstereoEncoder = null;
						}
					}
				}
			}); 
			aacThread.setName("aacConvertThread");
			aacThread.start();
			int index = 0;
			while(index < 2000) {
				index ++;
				AudioData audioData = audioData();
				aacData.add(audioData);
				mp3Data.add(audioData);
			}
			workingFlg = false;
			// このタイミングでデータをいれていく。
			mp3Thread.join();
			aacThread.join();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally{
			if(mp3stereo != null) {
				try {
					mp3stereo.close();
				}
				catch (Exception e) {
				}
				mp3stereo = null;
			}
			if(aacstereo != null) {
				try {
					aacstereo.close();
				}
				catch (Exception e) {
				}
				aacstereo = null;
			}
		}
	}
}
