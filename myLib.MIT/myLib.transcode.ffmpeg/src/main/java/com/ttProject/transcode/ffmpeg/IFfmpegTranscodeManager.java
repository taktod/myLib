package com.ttProject.transcode.ffmpeg;

import com.ttProject.transcode.ITranscodeManager;
import com.ttProject.transcode.ffmpeg.filestream.IUnitizer;
import com.ttProject.transcode.ffmpeg.filestream.IDeunitizer;

/**
 * ffmpegに変換させる動作マネージャー
 * @author taktod
 */
public interface IFfmpegTranscodeManager extends ITranscodeManager {
	/**
	 * UnitをStreamに変更するプログラムを設置します
	 * @param handler
	 */
	public void setDeunitizer(IDeunitizer handler);
	/**
	 * stream入力をUnitに分解するプログラムを設置します。
	 * @param handler
	 */
	public void setUnitizer(IUnitizer handler);
	/**
	 * 変換コマンドを設置します
	 * @param command
	 */
	public void registerCommand(String command) throws Exception;
}
