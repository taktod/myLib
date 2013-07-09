package com.ttProject.media.mpegts;

import java.nio.ByteBuffer;

import com.ttProject.media.IAnalyzer;
import com.ttProject.media.Unit;
import com.ttProject.nio.channels.IReadChannel;

public abstract class Packet extends Unit {
	private int type; // 識別子
	private int size; // データ長
	private int versionNumber; // バージョン番号
	private byte currentNextOrder; // nextOrder?
	private int sectionNumber; // セクション番号
	private int lastSectionNumber; // ラストセクション番号
	/**
	 * コンストラクタ
	 * @param position
	 */
	public Packet(int position) {
		// 大きさは188バイト固定
		super(position, 188);
	}
	@Override
	public void analyze(IReadChannel ch, IAnalyzer<?> analyzer)
			throws Exception {
		analyze(ch);
	}
	/**
	 * mpegtsのデータのheader部
	 * たいていのデータがもっている共通項となります。
	 * @param buffer
	 * @param tableSignature
	 * @return
	 */
	protected boolean analyzeHeader(ByteBuffer buffer, byte tableSignature) {
		if(buffer.get() != tableSignature) {
			// テーブルシグネチャが合いません。
			return false;
		}
		int data = buffer.getShort() & 0xFFFF;
		if(data >>> 12 != Integer.parseInt("1011", 2)) {
			// セクションシンタクス指示が一致しません。
			return false;
		}
		size = data & 0x0FFF;
		type = buffer.getShort() & 0xFFFF;
		data = buffer.get() & 0xFF;
		if(data >>> 6 != Integer.parseInt("11", 2)) {
			// 形式がおかしい。
			return false;
		}
		versionNumber = (data & 0x3F) >>> 1;
		currentNextOrder = (byte)(data & 0x01);
		sectionNumber = buffer.get() & 0xFF;
		lastSectionNumber = buffer.get() & 0xFF;
		return true;
	}
	public int getType() {
		return type;
	}
	public int getDataSize() {
		return size;
	}
	public int getVersionNumber() {
		return versionNumber;
	}
	public byte getCurrentNextOrder() {
		return currentNextOrder;
	}
	public int getSectionNumber() {
		return sectionNumber;
	}
	public int getLastSectionNumber() {
		return lastSectionNumber;
	}
}
