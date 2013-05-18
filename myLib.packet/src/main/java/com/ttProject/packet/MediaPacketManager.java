package com.ttProject.packet;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * MediaPacketManagerの共通動作部分抜きだし
 * @author taktod
 */
public abstract class MediaPacketManager implements IMediaPacketManager {
	/** 保持データ実体 */
	private ByteBuffer buffer = null;
	/** 現在処理中のパケット参照 */
	private IMediaPacket currentPacket = null;
	/** 処理済み書き込み経過時刻 */
	private float passedTime = 0;
	/** 各パケットの目標の長さ */
	private float duration = 2;
	/**
	 * {@inheritDoc}
	 */
	@Override
	public float getDuration() {
		return duration;
	}
	public void setDuration(float duration) {
		this.duration = duration;
	}
	/**
	 * パケットの解析処理
	 */
	@Override
	public List<IMediaPacket> getPackets(ByteBuffer data) {
		if(buffer != null) {
			int length = buffer.remaining() + data.remaining();
			ByteBuffer newBuffer = ByteBuffer.allocate(length);
			newBuffer.put(buffer);
			buffer = newBuffer;
			buffer.put(data);
			buffer.flip();
		}
		else {
			int length = data.remaining();
			ByteBuffer newBuffer = ByteBuffer.allocate(length);
			newBuffer.put(data);
			buffer = newBuffer;
			buffer.flip();
		}
		List<IMediaPacket> result = new ArrayList<IMediaPacket>();
		while(buffer.remaining() > 0) {
			IMediaPacket packet = analizePacket(buffer);
			if(packet == null) {
				break;
			}
			else {
				result.add(packet);
			}
		}
		return result;
	}
	/**
	 * 現在処理中のパケットを取得
	 */
	@Override
	public IMediaPacket getCurrentPacket() {
		return currentPacket;
	}
	/**
	 * パケットの中身解析処理
	 * @param buffer
	 * @return
	 */
	protected abstract IMediaPacket analizePacket(ByteBuffer buffer);
	/**
	 * 現在処理中のパケットを取得
	 * @param packet
	 */
	protected void setCurrentPacket(IMediaPacket packet) {
		currentPacket = packet;
	}
	/**
	 * 経過秒数を増やす
	 * @param time
	 */
	public void addPassedTime(float time) {
		passedTime += time;
	}
	/**
	 * 経過秒数を取得する。
	 * @return 秒数
	 */
	public float getPassedTime() {
		return passedTime;
	}
	public void reset() {
		passedTime = 0;
		currentPacket = null;
		buffer = null;
	}
}
