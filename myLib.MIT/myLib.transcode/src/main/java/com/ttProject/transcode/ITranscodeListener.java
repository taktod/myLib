package com.ttProject.transcode;

import java.util.List;

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
	public void receiveData(List<Unit> unit);
}
