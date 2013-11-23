package com.ttProject.transcode.xuggle.packet;

import java.util.ArrayList;
import java.util.List;

import com.ttProject.media.Unit;
import com.ttProject.media.mp3.Frame;
import com.ttProject.media.mp3.FrameAnalyzer;
import com.ttProject.media.mp3.frame.Mp3;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IStreamCoder;

/**
 * packetからmp3frameを取り出す
 * @author taktod
 */
public class Mp3Depacketizer implements IDepacketizer {
	/**
	 * mp3のframeに変換します。
	 */
	@Override
	public List<Unit> getUnits(IStreamCoder encoder, IPacket packet)
			throws Exception {
		if(!packet.isComplete()) {
			return null;
		}
		List<Unit> frameList = new ArrayList<Unit>();
		// packetデータからmp3をつくる
		FrameAnalyzer analyzer = new FrameAnalyzer();
		IReadChannel packetChannel = new ByteReadChannel(packet.getData().getByteArray(0, packet.getSize()));
		Frame mp3Frame = null;
		while((mp3Frame = analyzer.analyze(packetChannel)) != null) {
			if(mp3Frame instanceof Mp3) {
				frameList.add(mp3Frame);
			}
		}
		return frameList;
	}
}
