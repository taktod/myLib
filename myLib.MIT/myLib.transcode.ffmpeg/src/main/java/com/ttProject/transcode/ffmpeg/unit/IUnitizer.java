package com.ttProject.transcode.ffmpeg.unit;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * ffmpegの出力ストリームをUnitに変換するインターフェイス
 * こちらは、ほぼやることなし
 * @author taktod
 */
public interface IUnitizer {
	/**
	 * ffmpegの出力byteBufferからmediaUnitを取り出す動作
	 * @param buffer
	 * @return
	 * @throws Exception
	 */
	public List<?> getUnits(ByteBuffer buffer) throws Exception;
	/**
	 * 必要なくなったときの動作
	 */
	public void close();
}
