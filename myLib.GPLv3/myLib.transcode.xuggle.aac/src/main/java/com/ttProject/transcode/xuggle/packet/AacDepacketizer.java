package com.ttProject.transcode.xuggle.packet;

import java.util.ArrayList;
import java.util.List;

import com.ttProject.media.Unit;
import com.ttProject.media.aac.Frame;
import com.ttProject.media.aac.FrameAnalyzer;
import com.ttProject.media.aac.frame.Aac;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IStreamCoder;

/**
 * packetからadtsのaacを取り出す
 * @author taktod
 */
public class AacDepacketizer implements IDepacketizer {
	/**
	 * AacのFrameに変換します。
	 */
	@Override
	public List<Unit> getUnits(IStreamCoder encoder, IPacket packet)
			throws Exception {
		if(!packet.isComplete()) {
			return null;
		}
		List<Unit> frameList = new ArrayList<Unit>();
		// packetデータからAacをつくる。
		FrameAnalyzer analyzer = new FrameAnalyzer();
		IReadChannel packetChannel = new ByteReadChannel(packet.getData().getByteArray(0, packet.getSize()));
		Frame aacFrame = null;
		while((aacFrame = analyzer.analyze(packetChannel)) != null) {
			if(aacFrame instanceof Aac) {
				frameList.add(aacFrame);
			}
		}
		return frameList;
	}
}
