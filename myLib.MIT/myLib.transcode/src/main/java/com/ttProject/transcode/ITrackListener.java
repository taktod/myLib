package com.ttProject.transcode;

import java.util.List;

import com.ttProject.media.Unit;

/**
 * 各トラックごとの変換結果を受け取るリスナー
 * @author taktod
 */
public interface ITrackListener {
	/**
	 * 変換処理後のunitデータを受けとります。
	 * @param unit
	 */
	public void receiveData(List<Unit> unit);
	/**
	 * 終了処理
	 */
	public void close();
}
