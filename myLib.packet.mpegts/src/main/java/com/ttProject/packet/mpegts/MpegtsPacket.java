package com.ttProject.packet.mpegts;

import java.nio.ByteBuffer;

import com.ttProject.packet.MediaPacket;

@SuppressWarnings("unused")
public abstract class MpegtsPacket extends MediaPacket {
	/** マネージャー保持 */
	private final MpegtsPacketManager manager;
	/** 特に使わないであろうデータたち sizeはつかうか・・・ */
	private int type; // 識別子
	private int size; // データ長
	private int versionNumber; // バージョン番号
	private byte currentNextOrder; // nextOrder
	private int sectionNumber; // セクション番号
	private int lastSectionNumber; // ラストセクション番号
	/**
	 * コンストラクタ
	 * @param manager
	 */
	public MpegtsPacket(MpegtsPacketManager manager) {
		this.manager = manager;
	}
	/**
	 * マネージャー参照
	 * @return
	 */
	protected MpegtsPacketManager getManager() {
		return manager;
	}
	/**
	 * PIDを求める
	 * @param buffer
	 * @return
	 */
	protected int getPid(ByteBuffer buffer) {
		// なにかあったときに巻き戻すための位置取得
		int position = buffer.position();
		// 先頭を確認
		if(buffer.get() != 0x47) {
//			System.out.println("position:" + position);
			throw new RuntimeException("先頭が0x47になっていないとmpegtsとして成立していない。");
		}
		// pid取得
		int pid = buffer.getShort() & 0x1FFF;
		// buffer巻き戻し
		buffer.position(position);
		return pid;
	}
	/**
	 * ヘッダの解析(たいていのデータが持っているみたいです。)
	 * @return
	 */
	private boolean analizeHeader(ByteBuffer buffer, byte tableSignature) {
		if(buffer.get() != tableSignature) {
			// テーブルシグネチャが合いません。
			return false;
		}
		int data = buffer.getShort() & 0xFFFF;
		if(data >>> 12 != Integer.parseInt("1011", 2)) {
			// セクションシンタクス指示が一致しません。
			return false;
		}
		size = data &0x0FFF;
		type = buffer.getShort() & 0xFFFF;
		data = buffer.get() & 0xFF;
		if(data >>> 6 != Integer.parseInt("11", 2)) {
			// 形式がおかしい。
			return false;
		}
		versionNumber = (data & 0x3F) >>> 1;
		currentNextOrder = (byte)(data & 0x01);
		sectionNumber = buffer.get() & 0xFF;
		lastSectionNumber = buffer.get() & 0xFF;
		size -= 5;
		return true;
	}
	/**
	 * Patとしてデータを解析
	 * @param buffer
	 * @return
	 */
	protected boolean analizePat(ByteBuffer buffer) {
		int position = buffer.position();
		buffer.position(position + 5); // 5すすめる。
		if(!analizeHeader(buffer, (byte)0x00)) {
			buffer.position(position);
			throw new RuntimeException("ヘッダ部読み込み時に不正なデータを検出しました。");
		}
		// ディテール読み込み
		while(size > 4) {
			size -= 4;
			// 放送番組識別 16bit
			// 111 3bit (固定)
			// PIDデータ 13bit
			int data = buffer.getInt(); // ４バイト読み込む
			if((data & 0xF000) >>> 13 != Integer.parseInt("111", 2)) {
				// 固定bitが一致しない。
				buffer.position(position);
				throw new RuntimeException("ビットフラグが一致しない。");
			}
			if(data >>> 16 != 0) {
				// PMT PID
				// pmtなので、保持させる。
				manager.addPmtId(data & 0x1FFF); // dataが32bitになっているので、上位16bitについて確認するべきだとおもうけど。
			}
			else {
				// ネットワークPID
			}
		}
		// bufferの位置を戻しておく。
		buffer.position(position);
 		return true;
	}
	/**
	 * Pmtとしてデータを解析
	 * @param buffer
	 * @return
	 */
	protected boolean analizePmt(ByteBuffer buffer) {
		int position = buffer.position();
		buffer.position(position + 5); // 5すすめる。

		if(!analizeHeader(buffer, (byte)0x02)) {
			buffer.position(position);
			throw new RuntimeException("ヘッダ部の読み込み時に不正なデータを検出しました。");
		}
		int data;
		// 111 3bit(固定)
		// PCR_PID 13ビット 時刻主体になるパケット情報
		data = buffer.getShort() & 0xFFFF;
		if(data >>> 13 != Integer.parseInt("111", 2)) {
			buffer.position(position);
			throw new RuntimeException("PCRPID用の指示ビットがおかしいです。");
		}
		manager.setPcrId(data & 0x1FFF);
		// 1111 4bit(固定)
		// 番組情報長 12bit
		data = buffer.getShort() & 0xFFFF;
		if(data >>> 12 != Integer.parseInt("1111", 2)) {
			buffer.position(position);
			throw new RuntimeException("番組情報長用の指示ビットがおかしいです。");
		}
		int skipLength = data & 0x0FFF;
		// 番組情報
		int pos = buffer.position();
		buffer.position(pos + skipLength);
		size -= (4 + skipLength);
		// 以降ストリームデータ
		while(size > 4) {
			// ストリーム形式 8bit
			byte type = buffer.get();
			// 111 3bit
			// エレメンタリーPID 13bit
			data = buffer.getShort() & 0xFFFF;
			if(data >>> 13 != Integer.parseInt("111", 2)) {
				buffer.position(position);
				throw new RuntimeException("エレメンタリーID用の指示ビットがおかしいです。");
			}
			int pid = data & 0x1FFF;
			if(type == 0x1B) {
				// h.264用のトラック
				manager.addH264Id(pid);
			}
			manager.addMediaId(pid);
			// 1111 4bit
			// ES情報長 12bit
			data = buffer.getShort() & 0xFFFF;
			if(data >>> 12 != Integer.parseInt("1111", 2)) {
				buffer.position(position);
				throw new RuntimeException("ES情報長用の指示ビットがおかしいです。");
			}
			skipLength = data & 0x0FFF;
			// 任意
			pos = buffer.position();
			buffer.position(pos + skipLength);
			size -= (5 + skipLength);
		}
		// ポジションを戻しておく。
		buffer.position(position);
		return true;
	}
}
