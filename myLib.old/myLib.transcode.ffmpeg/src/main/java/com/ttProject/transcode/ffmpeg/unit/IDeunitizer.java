/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.transcode.ffmpeg.unit;

import java.nio.ByteBuffer;

import com.ttProject.media.Unit;
import com.ttProject.transcode.exception.FormatChangeException;

/**
 * unitデータをffmpegの入力用ストリームに変換するインターフェイス
 * こちらは音声のgapとかについて、よく考える必要あり。
 * @author taktod
 */
public interface IDeunitizer {
	/**
	 * mediaDataの正当性を確認します。
	 * @param unit
	 * @return true:このhandlerで処理します false:このhandlerで処理しません
	 * @throws FormatChangeException このhandlerで処理しますが、フォーマットデータがかわったので初期化すべき
	 */
	public boolean check(Unit unit) throws FormatChangeException;
	/**
	 * unitからffmpegに流し込むByteBufferを生成します
	 * @param unit
	 * @return
	 */
	public ByteBuffer getBuffer(Unit unit) throws Exception;
	/**
	 * 必要なくなったときの動作
	 */
	public void close();
}
