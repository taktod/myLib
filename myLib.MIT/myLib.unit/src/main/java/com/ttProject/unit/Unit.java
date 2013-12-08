package com.ttProject.unit;

import java.nio.ByteBuffer;

/**
 * すべてのメディアunitの基本となるクラス
 * こういうクラスの参照データは、abstractクラスに持たせておいたほうが見通しがよくなりそう。
 * 内容構成bitデータは、絶対に入るものは、宣言時にbitを構築しておく。
 * 必要があれば入るものは、nullをいれておく。
 * コンストラクタで入るものは、finalをつけておく。
 * @author taktod
 */
public abstract class Unit implements IUnit {
	/** データサイズ */
	private int size;
	/** 保持データ */
	private ByteBuffer data;
	/** pts値 */
	private long pts;
	/** timebase値*/
	private long timebase;
	/** データの更新を実施したかフラグ */
	private boolean update = false;
	/**
	 * なにか更新したときに印をつけておく
	 */
	protected final void update() {
		update = true;
	}
	/**
	 * データの更新がある場合にdataの中身の更新要求
	 * @throws Exception
	 */
	protected abstract void requestUpdate() throws Exception;
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getSize() {
		return size;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ByteBuffer getData() throws Exception {
		if(update) {
			requestUpdate();
		}
		return data;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public long getPts() {
		return pts;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public long getTimebase() {
		return timebase;
	}
	protected void setSize(int size) {
		this.size = size;
	}
	protected void setData(ByteBuffer data) {
		this.data = data;
		update = false;
	}
	protected void setPts(long pts) {
		this.pts = pts;
	}
	protected void setTimebase(long timebase) {
		this.timebase = timebase;
	}
}
