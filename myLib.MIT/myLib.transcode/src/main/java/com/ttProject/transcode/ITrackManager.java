package com.ttProject.transcode;

/**
 * 各トラックの処理マネージャー
 * @author taktod
 */
public interface ITrackManager {
	/**
	 * 変換後のデータを通知するlistenerの設定
	 * @param listener
	 */
	public void setTrackListener(ITrackListener listener);
	/**
	 * 動作ID参照
	 * @return id番号
	 */
	public int getId();
}
