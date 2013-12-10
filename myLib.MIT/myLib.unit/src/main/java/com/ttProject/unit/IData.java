package com.ttProject.unit;

import java.nio.ByteBuffer;

import com.ttProject.nio.channels.IReadChannel;

public interface IData {
	/**
	 * 保持データを応答します。
	 * (中身が全部はいっているものとします)
	 * @return
	 * @throws Exception
	 */
	public ByteBuffer getData() throws Exception;
	/**
	 * サイズを応答します。
	 * @return
	 */
	public int getSize();
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
