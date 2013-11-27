package com.ttProject.transcode.ffmpeg.filestream;

import com.ttProject.media.Unit;

/**
 * 対象トラック用のunitがどれであるか判定するインターフェイス
 * @author taktod
 */
public interface IUnitSelector {
	/**
	 * unitを確認する
	 * @param unit
	 * @return true:このトラックで処理するデータ false:このトラックで処理しないデータ
	 */
	public boolean check(Unit unit);
}
