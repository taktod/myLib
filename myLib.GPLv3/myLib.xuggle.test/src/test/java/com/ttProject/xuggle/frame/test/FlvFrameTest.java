package com.ttProject.xuggle.frame.test;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ttProject.container.flv.FlvTagAnalyzer;
import com.ttProject.container.flv.type.VideoTag;
import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IFileReadChannel;
import com.ttProject.unit.IAnalyzer;
import com.ttProject.unit.IUnit;
import com.ttProject.xuggle.frame.Packetizer;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.IVideoPicture;

/**
 * xuggleによるコンテナの変換を実行するテスト
 * @author taktod
 */
public class FlvFrameTest {
	/** 動作ロガー */
	private Logger logger = Logger.getLogger(FlvFrameTest.class);
	@Test
	public void flv1Test() throws Exception {
		convertTest(
			FileReadChannel.openFileReadChannel(
					Thread.currentThread().getContextClassLoader().getResource("flv1.flv")
			)
		);
	}
	private void convertTest(IFileReadChannel source) {
		try {
			IAnalyzer analyzer = new FlvTagAnalyzer();
			IUnit unit = null;
			IPacket packet = null;
			IStreamCoder decoder = null;
			Packetizer packetizer = new Packetizer();
			while((unit = analyzer.analyze(source)) != null) {
				if(unit instanceof VideoTag) {
					VideoTag vTag = (VideoTag)unit;
					logger.info(vTag.getFrame());
					decoder = packetizer.getDecoder(vTag.getFrame(), decoder);
					if(!decoder.isOpen()) {
						if(decoder.open(null, null) < 0) {
							throw new Exception("デコーダーが開けませんでした");
						}
					}
					packet = packetizer.getPacket(vTag.getFrame(), packet);
					IVideoPicture picture = IVideoPicture.make(decoder.getPixelType(), vTag.getWidth(), vTag.getHeight());
					int offset = 0;
					while(offset < packet.getSize()) {
						int bytesDecoded = decoder.decodeVideo(picture, packet, offset);
						if(bytesDecoded <= 0) {
							throw new Exception("データのデコードに失敗しました");
						}
						offset += bytesDecoded;
						if(picture.isComplete()) {
							logger.info(picture);
						}
					}
				}
			}
		}
		catch(Exception e) {
			logger.error("例外発生", e);
		}
		finally {
			if(source != null) {
				try {
					source.close();
				}
				catch(Exception e){}
				source = null;
			}
		}
	}
}
