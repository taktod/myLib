package com.ttProject.xuggle;

import java.util.List;

import com.ttProject.media.Unit;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IStreamCoder;

/**
 * xuggleのpacketをオブジェクトに戻すプログラムのインターフェイス
 * @author taktod
 *
 */
public interface IDepacketizer {
	/**
	 * packetからmediaUnitを取り出す動作
	 * @param encoder どういう変換をしたかという情報がはいっているのでエンコーダーオブジェクトを渡す。(sampleRateとかChannelとかとれる)
	 * @param packet 変換結果のデータ
	 * @return
	 * @throws Exception
	 */
	public List<Unit> getUnit(IStreamCoder encoder, IPacket packet) throws Exception;
}