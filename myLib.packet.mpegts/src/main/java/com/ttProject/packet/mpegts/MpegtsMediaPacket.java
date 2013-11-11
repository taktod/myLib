package com.ttProject.packet.mpegts;

import java.nio.ByteBuffer;

public class MpegtsMediaPacket extends MpegtsPacket {
	/** 開始tic保持 */
	private long startTic;
	/**
	 * コンストラクタ
	 * @param manager
	 */
	public MpegtsMediaPacket(MpegtsPacketManager manager) {
		super(manager);
		startTic = manager.getPassedTic();
	}
	/**
	 * ヘッダであるか応答する。
	 */
	@Override
	public boolean isHeader() {
		return false;
	}
	@Override
	public boolean analize(ByteBuffer buffer) {
		while(buffer.remaining() >= 188) {
			int pid = getPid(buffer);
			if(getManager().isPcrId(pid)) {
				// mediaIDだったら解析にまわす
				analizePcrPacket(buffer);
			}
			if(getManager().isH264Id(pid)) {
				// h.264のパケットの場合
				// キーパケットであるか確認する。
				if(isH264KeyPacket(buffer) && getBufferSize() > 0) {
					// バッファがある状態でキーパケットがきたら。次のパケットに進む。
					return true;
				}
			}
			// 188バイトのデータを追記します。
			byte[] data = new byte[188];
			buffer.get(data);
			getBuffer(188).put(data);
		}
		// パケットが不足したはず。
		return false;
	}
	/**
	 * pcrパケットを解析します。みつけた時間は現在時刻として登録しておきます
	 * @param buffer
	 * @return
	 */
	private void analizePcrPacket(ByteBuffer buffer) {
		int position = buffer.position();
		byte[] header = new byte[4];
		buffer.get(header);
		// syncByte
		if(header[0] != 0x47) {
			throw new RuntimeException("syncByteがおかしいです。");
		}
		int adaptationFlg = (header[3] & 0x20) >>> 5;
		if(adaptationFlg == 1) { // adaptationFlgがたっている場合は、追加情報に時間情報があるかもしれない
			// adaptationFieldについて解析する。
			int adaptationFieldLength = (buffer.get() & 0xFF);
			if(adaptationFieldLength != 0) {
				byte[] data = new byte[adaptationFieldLength];
				int pos = 0;
				buffer.get(data);
				int pcrFlg = (data[pos] & 0x10) >>> 4;
				if(pcrFlg == 1) { // pcrフラグがたっている場合は、時間の情報がある
					// 以下33ビット読み込んでデータをとる
					// 6バイト読み込む
					long tic = (data[++pos] & 0xFF);
					tic = (tic << 8) + (data[++pos] & 0xFF);
					tic = (tic << 8) + (data[++pos] & 0xFF);
					tic = (tic << 8) + (data[++pos] & 0xFF);
					tic = (tic << 1) + ((data[++pos] & 0x80) >>> 7);
					getManager().setTimeTic(tic);
					++pos;
					// durationを更新
					setDuration(getManager().getPassedTic() / 90000f - getManager().getPassedTime());
				}
			}
		}
		// 元に戻して次にまわす。
		buffer.position(position);
	}
	/**
	 * h.264のキーパケットであるか調べる
	 * @param buffer
	 * @return
	 */
	private boolean isH264KeyPacket(ByteBuffer buffer) {
		int position = buffer.position();
		byte[] header = new byte[4];
		buffer.get(header);
		// syncByte
		if(header[0] != 0x47) {
			throw new RuntimeException("syncByteがおかしいです。");
		}
		int adaptationFlg = (header[3] & 0x20) >>> 5;
		if(adaptationFlg == 1) { // adaptationFlgがたっている場合は、追加情報に時間情報があるかもしれない
			// adaptationFieldについて解析する。
			int adaptationFieldLength = (buffer.get() & 0xFF);
			if(adaptationFieldLength != 0) {
				byte[] data = new byte[adaptationFieldLength];
				int pos = 0;
				buffer.get(data);
				int randomAccessIndicator = (data[pos] & 0x40) >>> 6;
				int pcrFlg = (data[pos] & 0x10) >>> 4;
				if(pcrFlg == 1 && randomAccessIndicator == 1) { // pcrフラグがたっている場合は、時間の情報がある
					// adaptationField後のデータを読みこんでPESパケットヘッダであるか確認する。
					data = new byte[3];
					buffer.get(data);
					// メディアトラックであることを確認
					if(data[0] == 0x00 && data[1] == 0x00 && data[2] == 0x01) {
						// 経過時間を取得
						float passedTime = (getManager().getPassedTic() - startTic) / 90000f;
						// この分割する部分だけ、なんとかしておく必要あり。
						if(passedTime >= getManager().getDuration()) {
							// 経過時間が分割秒数を超えている場合は、次のパケットにすすむ
							getManager().addPassedTime(getDuration());
							buffer.position(position);
							return true;
						}
					}
				}
			}
		}
		// 元に戻して次にまわす。
		buffer.position(position);
		return false;
	}
}
