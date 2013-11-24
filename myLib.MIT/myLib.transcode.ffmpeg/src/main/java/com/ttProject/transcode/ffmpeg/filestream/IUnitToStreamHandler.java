package com.ttProject.transcode.ffmpeg.filestream;

import java.nio.ByteBuffer;

import com.ttProject.media.Unit;
import com.ttProject.transcode.exception.FormatChangeException;

/**
 * unitデータをffmpegの入力用ストリームに変換するインターフェイス
 * こちらは音声のgapとかについて、よく考える必要あり。
 * @author taktod
 */
public interface IUnitToStreamHandler {
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
	public ByteBuffer getBuffer(Unit unit);
}