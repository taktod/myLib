package com.ttProject.unit;

import java.nio.ByteBuffer;

/**
 * データのベースとなるクラス
 * @author taktod
 */
public abstract class Data implements IData {
	/** データサイズ */
	private int size = 0;
	/** 保持データ */
	private ByteBuffer data = null;
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
		return data.duplicate();
	}
	/**
	 * サイズ設定
	 * @param size
	 */
	protected void setSize(int size) {
		this.size = size;
	}
	/**
	 * データ設定
	 * @param data
	 */
	protected void setData(ByteBuffer data) {
		this.data = data;
		update = false;
	}
}
