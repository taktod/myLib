package com.ttProject.media.mpegts;

import com.ttProject.nio.channels.IReadChannel;

/**
 * パケットを解析する動作
 * @author taktod
 */
public class PacketAnalyzer implements IPacketAnalyzer {
	/** 解析用のマネージャー */
	private final MpegtsManager manager = new MpegtsManager();
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Packet analyze(IReadChannel ch) throws Exception {
		// データを取得したあとに、解析しておいてもいい。
		// ただし、patやpmtはすでに解析済み
		return manager.getUnit(ch);
	}
}
