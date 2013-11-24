package com.ttProject.transcode.ffmpeg.filestream;

import java.nio.ByteBuffer;
import java.util.List;

import com.ttProject.media.Unit;

/**
 * ffmpegの出力ストリームをUnitに変換するインターフェイス
 * こちらは、ほぼやることなし
 * @author taktod
 */
public interface IStreamToUnitHandler {
	/**
	 * ffmpegの出力byteBufferからmediaUnitを取り出す動作
	 * @param buffer
	 * @return
	 * @throws Exception
	 */
	public List<Unit> getUnits(List<Unit> units) throws Exception;
}
