package com.ttProject.packet.mpegts;

import java.nio.ByteBuffer;

/**
 * httpLiveStreamingで利用するヘッダ情報パケット
 * @author taktod
 */
public class MpegtsHeaderPacket extends MpegtsPacket {
	/**
	 * コンストラクタ
	 * @param manager
	 */
	public MpegtsHeaderPacket(MpegtsPacketManager manager) {
		super(manager);
	}
	/**
	 * ヘッダーであるか応答する。
	 */
	@Override
	public boolean isHeader() {
		return true;
	}
	@Override
	public boolean analize(ByteBuffer buffer) {
		// header処理
		while(buffer.remaining() >= 188) {
			boolean isPmtChecked = false;
			int pid = getPid(buffer);
			// pmtがくるまで取得しなければいけない。
			if(pid == MpegtsPacketManager.PATId) {
				analizePat(buffer);
			}
			else if(getManager().isPmtId(pid)) {
				analizePmt(buffer);
				isPmtChecked = true;
			}
			// 188バイトのデータを追記します。
			byte[] data = new byte[188];
			buffer.get(data);
			getBuffer(188).put(data);
			if(isPmtChecked) { // pmtの解析がおわっている場合は処理完了済み
				return true;
			}
		}
		// データが足りなくておわった。
		return false;
	}
}
