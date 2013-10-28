package com.ttProject.packet.mpegts;

import java.nio.ByteBuffer;

import com.ttProject.packet.MediaPacket;

/**
 * このパケットデータが指定された秒数分のファイルデータとなります。
 * Sdt Pat Pmt [keyFrame Audio innerFrame] [keyFrame Audio innerFrame]
 * となるようにしておきたいと思います。
 * @author taktod
 */
public class MpegtsPacket extends MediaPacket {
	/**
	 * データの解析を実施します。
	 */
	@Override
	public boolean analize(ByteBuffer buffer) {
		// 保持しているbufferを取り出す
		ByteBuffer buf = getBuffer(buffer.remaining());
		buf.put(buffer); // 追加しておく。
		return false;
	}
	/**
	 * ヘッダーデータであるか応答する。
	 */
	@Override
	public boolean isHeader() {
		return false;
	}
}
