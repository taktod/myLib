/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 */
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
	 * @param unit 確認するデータunit
	 * @return true:問題ない場合 false:処理対象でない場合
	 * @throws FormatChangeException データフォーマットが変更していて処理できない場合
	 */
	public boolean check(Unit unit) throws FormatChangeException;
	/**
	 * packetデータを応答します
	 * @param unit メディアUnit
	 * @param packet 既存のpacketメモリーを使い回す場合(使い回すとGCを遅らせることが可能です)
	 * @return 生成したパケットデータを応答します
	 * @throws Exception
	 */
	public IPacket getPacket(Unit unit, IPacket packet) throws Exception;
	/**
	 * デコーダーを応答します
	 * @return 処理パケットに対応するデコーダーを応答
	 * @throws Exception
	 */
	public IStreamCoder createDecoder() throws Exception;
	/**
	 * 後始末
	 */
	public void close();
}
