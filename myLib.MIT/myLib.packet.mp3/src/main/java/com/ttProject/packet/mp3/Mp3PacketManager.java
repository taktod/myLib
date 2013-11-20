package com.ttProject.packet.mp3;

import java.nio.ByteBuffer;

import com.ttProject.packet.IMediaPacket;
import com.ttProject.packet.MediaPacketManager;

public class Mp3PacketManager extends MediaPacketManager {
	/** 経過フレーム数 */
	private int frameCount = 0;
	/**
	 * 経過フレーム数を取得する。
	 * @return
	 */
	public int getFrameCount() {
		return frameCount;
	}
	/**
	 * 経過フレーム数をインクリメントする。
	 */
	public void addFrameCount() {
		this.frameCount ++;
	}
	/**
	 * 拡張子指定
	 */
	@Override
	public String getExt() {
		return ".mp3";
	}
	@Override
	public String getHeaderExt() {
		return ".m3u8";
	}
	/**
	 * パケットの内容を解析する。
	 */
	@Override
	protected IMediaPacket analizePacket(ByteBuffer buffer) {
		IMediaPacket packet = getCurrentPacket();
		if(packet == null) {
			packet = new Mp3MediaPacket(this);
		}
		if(packet.analize(buffer)) {
			setCurrentPacket(null);
			return packet;
		}
		else {
			setCurrentPacket(packet);
			return null;
		}
	}
}
