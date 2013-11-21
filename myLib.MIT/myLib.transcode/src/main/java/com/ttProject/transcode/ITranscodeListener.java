package com.ttProject.transcode;

import com.ttProject.media.Unit;

/**
 * 変換の結果を受け取るリスナー
 * @author taktod
 */
public interface ITranscodeListener {
	/**
	 * 変換処理後のデータをうけとります。
	 * @param unit
	 */
	public void receiveData(Unit unit);
	/**
	 * 例外発生時に例外を受け取ります。
	 * @param e
	 */
	public void exceptionCaught(Exception e);
}
