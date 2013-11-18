package com.ttProject.segment;

public interface ISegmentManager {
	/**
	 * データの書き込み
	 * @param target
	 * @param http
	 * @param duration
	 * @param index
	 * @param endFlg
	 */
	public void writeData(String target, String http, float duration, int index, boolean endFlg);
	/**
	 * 終端を書き込みます。
	 */
	public void writeEnd();
}
