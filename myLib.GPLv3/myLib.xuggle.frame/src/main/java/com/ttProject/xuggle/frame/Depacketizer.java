package com.ttProject.xuggle.frame;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ttProject.frame.AudioFrame;
import com.ttProject.frame.IAnalyzer;
import com.ttProject.frame.IFrame;
import com.ttProject.frame.VideoFrame;
import com.ttProject.frame.aac.AacFrameAnalyzer;
import com.ttProject.frame.h264.NalAnalyzer;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.xuggle.xuggler.ICodec.Type;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IStreamCoder;

/**
 * IPacket -> frame変換クラス
 * @author taktod
 */
public class Depacketizer {
	/** ロガー */
	private Logger logger = LoggerFactory.getLogger(Depacketizer.class);
	private IAnalyzer analyzer = null;
	// どうやってやろう？
	public IFrame getFrame(IStreamCoder encoder, IPacket packet) throws Exception {
		IReadChannel channel = new ByteReadChannel(packet.getData().getByteArray(0, packet.getSize()));
		try {
			if(encoder.getCodecType() == Type.CODEC_TYPE_AUDIO) {
				logger.info("audioframe解析はじめ:" + (packet.getPts() * packet.getTimeBase().getDouble()));
				switch(encoder.getCodecID()) {
				case CODEC_ID_AAC:
					if(analyzer == null || !(analyzer instanceof AacFrameAnalyzer)) {
						analyzer = new AacFrameAnalyzer();
					}
					// AACの
					break;
				default:
					throw new Exception("処理不能なコーデックでした:" + encoder.getCodecID());
				}
				AudioFrame result = null;
				while((result = (AudioFrame)analyzer.analyze(channel)) != null) {
					// ここでframeにtimestampをつけないとだめっぽい
					result.setPts(packet.getPts());
					result.setTimebase(packet.getTimeBase().getDenominator());
					logger.info("frame: {}, timestamp: {}", result, result.getPts());
				}
				return result;
			}
			else if(encoder.getCodecType() == Type.CODEC_TYPE_VIDEO) {
				logger.info("videoframe解析はじめ:" + (packet.getPts() * packet.getTimeBase().getDouble()));
				switch(encoder.getCodecID()) {
				case CODEC_ID_H264:
					// h264のNal解析を走らせる必要がある。
					if(analyzer == null || !(analyzer instanceof NalAnalyzer)) {
						analyzer = new NalAnalyzer();
					}
					break;
				default:
					throw new Exception("処理不能なコーデックでした:" + encoder.getCodecID());
				}
				VideoFrame result = null; // sliceかsliceIDRのみ応答すればよし
				while((result = (VideoFrame)analyzer.analyze(channel)) != null) {
					// ここでframeにtimestampをつけないとだめっぽい
					result.setPts(packet.getPts());
					result.setTimebase(packet.getTimeBase().getDenominator());
					logger.info("frame: {}, timestamp: {}", result, result.getPts());
				}
			}
		}
		finally{
			channel.close();
		}
		return null;
	}
}
