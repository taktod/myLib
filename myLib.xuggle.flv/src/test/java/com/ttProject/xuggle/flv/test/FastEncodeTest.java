package com.ttProject.xuggle.flv.test;

import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.concurrent.LinkedBlockingQueue;

import com.ttProject.media.flv.FlvHeader;
import com.ttProject.media.flv.ITagAnalyzer;
import com.ttProject.media.flv.Tag;
import com.ttProject.media.flv.TagAnalyzer;
import com.ttProject.media.flv.tag.VideoTag;
import com.ttProject.media.raw.VideoData;
import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IFileReadChannel;
import com.ttProject.xuggle.flv.FlvDepacketizer;
import com.ttProject.xuggle.flv.FlvPacketizer;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IPixelFormat;
import com.xuggle.xuggler.IRational;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.IVideoPicture;
import com.xuggle.xuggler.IVideoResampler;
import com.xuggle.xuggler.IStreamCoder.Direction;
import com.xuggle.xuggler.video.ConverterFactory;
import com.xuggle.xuggler.video.IConverter;

/**
 * 高速エンコード動作のテスト
 * とりあえず動きの大きいもので試してみたいのでマリオの動画でいってみたいとおもいます。
 * @author taktod
 */
public class FastEncodeTest {
	boolean working = true;
	private Tag sourceTag = null;
//	@Test
	public void test1() {
		IFileReadChannel source = null;
		IStreamCoder decoder = null;
		FileChannel target = null;
		try {
			target = new FileOutputStream("mario_a.flv").getChannel();
			source = FileReadChannel.openFileReadChannel("mario.flv");
			FlvHeader flvHeader = new FlvHeader();
			flvHeader.setVideoFlg(true);
			flvHeader.setAudioFlg(false);
			target.write(flvHeader.getBuffer());
			flvHeader.analyze(source);
			ITagAnalyzer analyzer = new TagAnalyzer();
			FlvPacketizer packetizer = new FlvPacketizer();
			IPacket packet = null;
			Tag tag = null;
			final LinkedBlockingQueue<VideoData> queue = new LinkedBlockingQueue<VideoData>();
			final FileChannel ttarget = target;
			// 変換スレッドの準備
			Thread encodeThread = new Thread(new Runnable() {
				@Override
				public void run() {
					IStreamCoder encoder = null;
					IConverter converter = ConverterFactory.createConverter(new BufferedImage(640, 360, BufferedImage.TYPE_3BYTE_BGR), IPixelFormat.Type.YUV420P);
					try {
						encoder = IStreamCoder.make(Direction.ENCODING, ICodec.ID.CODEC_ID_FLV1);
						IRational frameRate = IRational.make(15, 1); // 15fps
						encoder.setNumPicturesInGroupOfPictures(30); // gopを30にしておく。keyframeが30枚ごとになる。
						encoder.setBitRate(850000); // 250kbps
						encoder.setBitRateTolerance(9000);
						encoder.setPixelType(IPixelFormat.Type.YUV420P);
						encoder.setWidth(640);
						encoder.setHeight(360);
						encoder.setGlobalQuality(10);
						encoder.setFrameRate(frameRate);
						encoder.setTimeBase(IRational.make(1, 1000)); // 1/1000設定(flvはこうなるべき)
						if(encoder.open(null, null) < 0) {
							throw new RuntimeException("エンコーダーが開けませんでした。");
						}
						IPacket packet = IPacket.make();
						FlvDepacketizer depacketizer = new FlvDepacketizer();
						while(working || queue.size() > 0) {
							VideoData vData = queue.take();
							IVideoPicture picture = converter.toPicture(vData.getImage(), vData.getTimestamp() * 1000);
							if(encoder.encodeVideo(packet, picture, 0) < 0) {
								throw new Exception("エンコード失敗");
							}
							if(packet.isComplete()) {
								Tag destTag = null;
								for(Tag tag : depacketizer.getTag(encoder, packet)) {
									ttarget.write(tag.getBuffer());
									destTag = tag;
								}
								if(sourceTag != null) {
									System.out.println("ずれ：" + (sourceTag.getTimestamp() - destTag.getTimestamp()));
								}
							}
						}
					}
					catch (Exception e) {
						e.printStackTrace();
					}
					finally {
						if(encoder != null) {
							encoder.close();
							encoder = null;
						}
						working = false;
						queue.clear();
					}
				}
			});
			encodeThread.setName("エンコードスレッド");
			encodeThread.start();
			long startTime = -1;
			while((tag = analyzer.analyze(source)) != null) {
				if(tag instanceof VideoTag) {
					sourceTag = tag;
					long now = System.currentTimeMillis();
					if(startTime == -1) {
						startTime = now;
					}
					// 現在時刻
					long current = now - startTime;
					if(tag.getTimestamp() > current) {
						System.out.println("tag:" + tag.getTimestamp() + " sleepTime:" + (tag.getTimestamp() - current));
						Thread.sleep(tag.getTimestamp() - current);
					}
					packet = packetizer.getPacket(tag);
					if(packet == null) {
						continue;
					}
					if(decoder == null) {
						decoder = packetizer.createVideoDecoder();
					}
					int offset = 0;
					IVideoPicture picture = IVideoPicture.make(decoder.getPixelType(), decoder.getWidth(), decoder.getHeight());
					while(offset < packet.getSize()) {
						int bytesDecoded = decoder.decodeVideo(picture, packet, offset);
						if(bytesDecoded <= 0) {
							throw new Exception("デコード中に問題が発生しました。");
						}
						offset += bytesDecoded;
						if(picture.isComplete()) {
							IVideoPicture newPic = picture;
							if(picture.getPixelType() != IPixelFormat.Type.BGR24) {
								IVideoResampler resampler = IVideoResampler.make(decoder.getWidth(), decoder.getHeight(), IPixelFormat.Type.BGR24, decoder.getWidth(), decoder.getHeight(), decoder.getPixelType());
								newPic = IVideoPicture.make(resampler.getOutputPixelFormat(), picture.getWidth(), picture.getHeight());
								if(resampler.resample(newPic, picture) < 0) {
									throw new Exception("リサンプル失敗");
								}
							}
							if(!working) {
								break;
							}
							IConverter converter = ConverterFactory.createConverter("XUGGLER-BGR-24", newPic);
							BufferedImage image = converter.toImage(newPic);
							queue.add(new VideoData(image, tag.getTimestamp()));
						}
					}
					if(!working) {
						break;
					}
				}
			}
			// 処理の途中でもぶつ切りにしてやる。
			encodeThread.interrupt();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if(decoder != null) {
				decoder.close();
				decoder = null;
			}
			if(source != null) {
				try {
					source.close();
				}
				catch (Exception e) {
				}
				source = null;
			}
			if(target != null) {
				try {
					target.close();
				}
				catch (Exception e) {
				}
				target = null;
			}
		}
	}
}
