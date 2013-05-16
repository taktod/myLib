package com.ttProject.packet.flv;

import java.nio.ByteBuffer;
import java.util.List;

import com.ttProject.packet.IMediaPacket;
import com.ttProject.packet.MediaPacketManager;

/**
 * このプログラムでは、共有する情報を保持しておく必要がある。
 * @author taktod
 */
public class FlvPacketManager extends MediaPacketManager {
	public static final byte AUDIO_TAG = 0x08;
	public static final byte VIDEO_TAG = 0x09;
	public static final byte META_TAG  = 0x12;
	public static final byte FLV_TAG   = 0x46;
	public static final byte[] flvHeader = {
			0x46, 0x4C, 0x56,
			0x01,
			0x05,
			0x00, 0x00, 0x00, 0x09,
			0x00, 0x00, 0x00, 0x00};
	private long currentPos = 0; // 処理の現在位置(時刻)
	public void setCurrentPos(long pos) {
		currentPos = pos;
	}
	public long getCurrentPos() {
		return currentPos;
	}
	/** ヘッダパケット保持 */
	private FlvHeaderPacket headerPacket = null;
	private int CRC = 0x00000000;
	public int getCRC() {
		return CRC;
	}
	@Override
	public void reset() {
		currentPos = 0;
		headerPacket = null;
		super.reset();
	}
	/**
	 * 拡張子取得
	 */
	@Override
	public String getExt() {
		return ".flv";
	}
	@Override
	public String getHeaderExt() {
		return ".flh";
	}
	/**
	 * パケットを取得します。
	 */
	@Override
	public List<IMediaPacket> getPackets(ByteBuffer data) {
		List<IMediaPacket> result = super.getPackets(data);
		if(!headerPacket.isSaved()) {
			result.add(0, headerPacket);
		}
		return result;
	}
	/**
	 * パケットの内容を解析します。
	 */
	@Override
	protected IMediaPacket analizePacket(ByteBuffer buffer) {
		IMediaPacket packet = getCurrentPacket();
		if(packet == null) {
			if(headerPacket == null) {
				headerPacket = new FlvHeaderPacket(this);
			}
			packet = new FlvMediaPacket(this, headerPacket);
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
