/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.packet;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * TODO おわったときの残っているbufferの処理が抜けていますので、なんとかしなきゃだめ。
 * 適当な関数をつくって動作できるようにしておかなければ・・・
 * とりあえず、jsegmenterとconvertWorker_mvnで利用しているプログラムとなるので、注意が必要。s
 * @author taktod
 */
public interface IMediaPacketManager {
	/**
	 * データをいれると、書き込みOKになったパケットデータを応答します。
	 * @param buffer
	 * @return データのリスト
	 */
	public List<IMediaPacket> getPackets(ByteBuffer buffer);
	/**
	 * 現在処理中のパケットを応答します。
	 * @return
	 */
	public IMediaPacket getCurrentPacket();
	/**
	 * 拡張子を取得する。
	 * @return
	 */
	public String getExt();
	/**
	 * headerの拡張子を取得する。
	 * @return
	 */
	public String getHeaderExt();
	/**
	 * 目標packet長取得
	 * @return
	 */
	public float getDuration();
}
