package com.ttProject.chunk;

import com.ttProject.media.Unit;

/**
 * MediaデータをこのManagerの継承クラスに渡すとchunkに分割して応答します。
 * 主にhlsとかで利用する予定。
 * @author taktod
 */
public interface IMediaChunkManager {
	/**
	 * メディアデータのunitをいれると対象クラス用のデータがでてくる
	 * chunkを作るのにデータが足りない場合はnullが帰ってきます。
	 * @param unit
	 * @return
	 */
	public IMediaChunk getChunk(Unit unit) throws Exception;
	/**
	 * 現在処理中のchunkを応答する。
	 * @return
	 */
	public IMediaChunk getCurrentChunk();
	/**
	 * 利用拡張子を応答する
	 * @return
	 */
	public String getExt();
	/**
	 * ヘッダ用のデータの利用拡張子を応答する。
	 * @return
	 */
	public String getHeaderExt();
	/**
	 * 分割長さを参照します。
	 * @return
	 */
	public float getDuration();
	/**
	 * 分割長さを設定します。
	 * @param duration
	 * @return
	 */
	public void setDuration(float duration);
	/**
	 * 現在の保持データを破棄して終了する。
	 * @return 残っているmediaChunkがある場合はここで応答しなければいけない。
	 */
	public IMediaChunk close();
}
