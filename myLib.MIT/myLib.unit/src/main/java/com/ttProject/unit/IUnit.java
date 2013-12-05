package com.ttProject.unit;

import java.nio.ByteBuffer;

/**
 * 各unitの最少単位(flvのtagとかh264のframeとか)
 * @author taktod
 */
public interface IUnit {
	/**
	 * サイズを応答します。
	 * @return
	 */
	public long getSize();
	/**
	 * 関連づけられたtimestampを応答します
	 * @return
	 */
	public long getTimestamp();
	/**
	 * 保持データを応答します。
	 * (中身が全部はいっているものとします)
	 * @return
	 */
	public ByteBuffer getData();
}
