package com.ttProject.xuggle.flv.test;

import org.junit.Test;

import com.ttProject.media.flv.FlvHeader;
import com.ttProject.media.flv.ITagAnalyzer;
import com.ttProject.media.flv.Tag;
import com.ttProject.media.flv.TagAnalyzer;
import com.ttProject.media.flv.tag.VideoTag;
import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IFileReadChannel;
import com.ttProject.xuggle.flv.FlvPacketizer;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.IVideoPicture;

/**
 * デコード動作のテスト
 * @author taktod
 *
 */
public class DecodeTest {
	@Test
	public void h263Test() {
		decodeTest("/home/xxx/h263.flv");
	}
	@Test
	public void vp6Test() {
		decodeTest("/home/xxx/vp6.flv");
	}
	@Test
	public void avcTest() {
		decodeTest("/home/xxx/avc.flv");
	}
	private void decodeTest(String target) {
		try {
			IFileReadChannel source = FileReadChannel.openFileReadChannel(target);
			FlvHeader flvheader = new FlvHeader();
			flvheader.analyze(source);
			System.out.println(flvheader);
			ITagAnalyzer analyzer = new TagAnalyzer();
			// sourceをそのまま解析する。
			FlvPacketizer packetizer = new FlvPacketizer();
			IStreamCoder decoder = null;
			IPacket packet = null;
			Tag tag = null;
			while((tag = analyzer.analyze(source)) != null) {
				if(tag instanceof VideoTag) {
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
							System.out.println(picture);
						}
					}
				}
			}
			decoder.close();
			source.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
