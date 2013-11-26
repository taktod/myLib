package com.ttProject.transcode;

import com.ttProject.media.Unit;

/**
 * 変換マネージャー
 * @author taktod
 */
public interface ITranscodeManager {
	/**
	 * 例外取得の設定
	 * @param listener
	 */
	public void addExceptionListener(IExceptionListener listener);
	/**
	 * 変換を実行
	 * @param unit 対応メディアunit
	 */
	public void transcode(Unit unit) throws Exception;
	/**
	 * トラックマネージャーを取得します
	 * @param id
	 * @return
	 * /
	public ITrackManager getTrackManager(int id);
	/**
	 * 新しいtrackManagerを生成して応答します
	 * @return
	 * /
	public ITrackManager addNewTrackManager() throws Exception;
	/**
	 * 後始末
	 */
	public void close();
}
