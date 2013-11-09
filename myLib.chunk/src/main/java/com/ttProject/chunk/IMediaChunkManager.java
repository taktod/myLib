package com.ttProject.chunk;

import java.util.List;

import com.ttProject.media.Unit;

/**
 * MediaデータをこのManagerの継承クラスに渡すとchunkに分割して応答します。
 * 主にhlsとかで利用する予定。
 * @author taktod
 */
public interface IMediaChunkManager {
	/**
	 * メディアデータのunitをいれると対象クラス用のデータがでてくる
	 * @param unit
	 * @return
	 */
	public List<IMediaChunk> getChunks(Unit unit);
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
	 * 現在の処理済み時刻を応答します。
	 * @return
	 */
	public float getDuration();
}
