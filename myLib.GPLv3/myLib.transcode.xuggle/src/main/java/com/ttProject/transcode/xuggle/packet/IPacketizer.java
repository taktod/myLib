package com.ttProject.transcode.xuggle.packet;

import com.ttProject.media.Unit;
import com.ttProject.transcode.exception.FormatChangeException;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IStreamCoder;

/**
 * mediaのunitデータをxuggleのpacketに変換するプログラムのインターフェイス
 * @author taktod
 */
public interface IPacketizer {
	/**
	 * mediaDataの正当性を確認します。
	 * 正当でない場合はあらかじめエラーを返します。
	 * @param unit
	 * @return
	 */
	public boolean check(Unit unit) throws FormatChangeException;
	/**
	 * packetデータを作成して応答します
	 * @param unit メディアunit
	 * @param packet 既存のpacketメモリーを使い回す場合(使い回すとGCを遅らせることが可能です)
	 * @return
	 * @throws Exception
	 */
	public IPacket getPacket(Unit unit, IPacket packet) throws Exception;
	/**
	 * デコーダーを応答します
	 * @return
	 * @throws Exception
	 */
	public IStreamCoder createDecoder() throws Exception;
}
