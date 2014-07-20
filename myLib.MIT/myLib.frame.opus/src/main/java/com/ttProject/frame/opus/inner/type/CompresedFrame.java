/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.opus.inner.type;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import com.ttProject.util.HexUtil;

/**
 * CompressedFrame
 * @author taktod
 * Opusのframeデータはフレームの中に更に小さなフレームがはいっているみたいです。
 * 大本のサイズはsizeとします。
 * ただしこのフレームの解析は次のようになります。
 * TOCcが0 そのまま１フレーム(size - 1がCompressedFrame)
 * TOCcが1 半分に分割して２フレームになる((size - 1) / 2がCompressedFrameで２つある)
 * TOCcが2と3の場合はややこしいので、とりあえず後で考えることにします。
 */
public class CompresedFrame {
	/** 動作ロガー */
	private Logger logger = Logger.getLogger(CompresedFrame.class);
	/** 保持データ */
	private final ByteBuffer buffer;
	/**
	 * コンストラクタ
	 */
	public CompresedFrame(ByteBuffer buffer) {
		this.buffer = buffer;
	}
	@Override
	public String toString() {
		StringBuilder data = new StringBuilder();
		data.append("MUFrame:");
		data.append(HexUtil.toHex(buffer));
		return data.toString();
	}
}
