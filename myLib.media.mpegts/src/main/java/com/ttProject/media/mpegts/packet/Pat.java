package com.ttProject.media.mpegts.packet;

import java.nio.ByteBuffer;

import com.ttProject.media.mpegts.Packet;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.util.BufferUtil;

public class Pat extends Packet {
	public Pat() {
		this(0);
	}
	public Pat(int position) {
		super(position);
	}
	@Override
	public void analyze(IReadChannel ch) throws Exception {
		// 直接ここにくるはず。
		// 先頭の3バイトはすでに読み込み済みになっているはず。
		ByteBuffer buffer = BufferUtil.safeRead(ch, 185);
		// すでに3バイトすすんでいるところから解析するので、2バイトスキップさせる必要あり。
		buffer.position(2);
		if(!analyzeHeader(buffer, (byte)0x00)) {
			throw new RuntimeException("ヘッダ部読み込み時に不正なデータを検出しました。");
		}
		int size = getDataSize(); // 内部の実データサイズを取得しておく。
		size -= 5; // はじめの５バイトはデータとして読み込み済みのはず。(headerで取得できる情報)
		// 実データがいくつあるか確認する。
		while(size > 4) {
			size -= 4;
			// 放送番組識別 16bit
			int data = buffer.getInt(); // 4バイト読み込んで処理にまわしておく。
			// 111 3bit(固定)
			if((data & 0xF000) >>> 13 != Integer.parseInt("111", 2)) {
				// 固定bitが一致しない
				throw new Exception("固定bitが一致しない");
			}
			// PIDデータ13ビット
			if(data >>> 16 != 0) { // 先頭のbitフラグが立っているので0にならないことはありえないと思う。
			// if((data & 0x1FFF0000) >> 16 != 0)
				// PMT pid
				System.out.println("pmtPid:" + (data & 0x1FFF));
			}
			else {
				// ネットワークPID
			}
		}
	}
}
