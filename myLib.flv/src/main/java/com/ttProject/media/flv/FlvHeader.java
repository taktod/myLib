package com.ttProject.media.flv;

import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

import com.ttProject.nio.channels.IFileReadChannel;
import com.ttProject.util.BufferUtil;

/**
 * flvのHeader処理
 * @author taktod
 */
public class FlvHeader {
	/** audioデータがあるかフラグ */
	private boolean audioFlg;
	/** videoデータがあるかフラグ */
	private boolean videoFlg;
	/** サンプルヘッダーバイトデータ */
	private static final byte[] FLV_HEADER = {
		'F', 'L', 'V', 0x01, 0x05, 0x00, 0x00, 0x00, 0x09, 0x00, 0x00, 0x00, 0x00
	};
	/**
	 * コンストラクタ
	 */
	public FlvHeader() {
		videoFlg = false;
		audioFlg = false;
	}
	/**
	 * 解析実施
	 * @param ch
	 * @throws Exception
	 */
	public void analyze(IFileReadChannel ch) throws Exception {
		ch.position(0);
		// 先頭の13バイトを読み込んで解析しておく。
		ByteBuffer buffer = BufferUtil.safeRead(ch, 13);
		byte[] tag = new byte[13];
		buffer.get(tag);
		for(int i = 0;i < 13;i ++) {
			if(i != 4 && tag[i] != FLV_HEADER[i]) {
				throw new Exception("headerがおかしいです。");
			}
			else {
				audioFlg = ((tag[i] & 0x04) != 0x00);
				videoFlg = ((tag[i] & 0x01) != 0x00);
			}
		}
	}
	/**
	 * audioトラックがあるかどうか
	 * @return
	 */
	public boolean hasAudio() {
		return audioFlg;
	}
	/**
	 * videoトラックがあるかどうか
	 * @return
	 */
	public boolean hasVideo() {
		return videoFlg;
	}
	/**
	 * audioフラグセット
	 * @param flg
	 */
	public void setAudioFlg(boolean flg) {
		audioFlg = flg;
	}
	/**
	 * videoフラグセット
	 * @param flg
	 */
	public void setVideoFlg(boolean flg) {
		videoFlg = flg;
	}
	/**
	 * ファイルへの書き込み
	 * @param target
	 * @throws Exception
	 */
	public void write(WritableByteChannel target) throws Exception {
		ByteBuffer buffer = ByteBuffer.allocate(13);
		buffer.put(FLV_HEADER);
		buffer.position(4);
		byte flg = 0x00;
		if(audioFlg) {
			flg |= 0x04;
		}
		if(videoFlg) {
			flg |= 0x01;
		}
		buffer.put(flg);
		buffer.position(13);
		buffer.flip();
		target.write(buffer);
	}
}
