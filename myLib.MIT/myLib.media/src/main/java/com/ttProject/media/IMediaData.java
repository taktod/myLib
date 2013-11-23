package com.ttProject.media;

import java.nio.ByteBuffer;

/**
 * メディアデータ
 */
public interface IMediaData {
	/**
	 * pts値参照
	 * @return
	 */
	public long getPts();
	/**
	 * dts値参照
	 * @return
	 */
	public long getDts();
	/**
	 * timestampのunitの単位を決定します。
	 * 秒単位 1/1000秒の場合は0.001
	 * @return
	 */
	public double getTimebase();
	/**
	 * 生データを参照します。
	 * @return
	 */
	public ByteBuffer getRawData() throws Exception;
}
