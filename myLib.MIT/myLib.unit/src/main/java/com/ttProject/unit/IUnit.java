package com.ttProject.unit;

import java.nio.ByteBuffer;

import com.ttProject.nio.channels.IReadChannel;

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
	 * 保持データを応答します。
	 * (中身が全部はいっているものとします)
	 * @return
	 * @throws Exception
	 */
	public ByteBuffer getData() throws Exception;
	/**
	 * pts値(presentationTimestamp)
	 * @return
	 */
	public long getPts();
	/**
	 * unitのtimebase(時間の単位を記入しておきます。)
	 * flvの場合は1ミリ秒ベースなので1000
	 * mpegtsの場合は1/90000秒ベースなので90000といった感じ。(性格には、1/2400000?のもあるけど)
	 * @return
	 */
	public long getTimebase();
	/**
	 * データを最小限、読み込む動作
	 * @throws Exception
	 */
	public void minimumLoad(IReadChannel channel) throws Exception;
	/**
	 * データをすべて読み込む動作
	 * @param channel
	 * @throws Exception
	 */
	public void load(IReadChannel channel) throws Exception;
}
