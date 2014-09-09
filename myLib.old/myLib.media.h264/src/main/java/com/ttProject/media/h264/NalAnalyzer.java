/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.media.h264;

import java.nio.ByteBuffer;

import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.util.BufferUtil;

/**
 * nalデータの解析を実行します。
 * ただし、nalデータの印の部分00 00 00 01 or 00 00 01の部分には興味がないので、
 * データとしては、その部分を省いたデータを応答することにします。
 * 
 * なおこれはまだ作成する必要はなさそう。(mpegtsの読み込みくらいしか使いどころなさそうだから、
 * 他のデータmp4やflvはnalではない方法をつかっているみたいです。)
 * @author taktod
 *
 */
public class NalAnalyzer extends FrameAnalyzer {
	@Override
	public Frame analyze(IReadChannel ch) throws Exception {
		// cacheBufferを利用した動作だと、IReadChannelの位置が移動してしまってあとでやり直しが効かないので、調整が必要。
		// 00 00 01で切って、00 00 01になるまで読み込んでおく。
		// 読み込みはじめに00 00 01以外がくる場合は、そこからframeがはじまると解釈すればよさそう。
		Short lastData = null;
		ByteBuffer buf = ByteBuffer.allocate(ch.size() - ch.position());
		// データを読み込んでいく。
		while(ch.size() - ch.position() > 1) {
			// shortで確認した方がいい可能性もある。
			short data = BufferUtil.safeRead(ch, 2).getShort();
			// 00 00 00 01もしくは 00 00 01がnalの分岐点
			// よってshort = 0になった場合に注意して2バイト先までbyteでデータを取得すればよさそう。
			if(data == 0) {
				// 前のデータを確認した、下の部分が00だったら追加するデータがアレになる。
				byte firstByte, secondByte;
				firstByte = BufferUtil.safeRead(ch, 1).get();
				if(firstByte == 1) {
					checkLastData(buf, lastData);
					buf.flip();
					if(buf.remaining() == 0) {
						// データがない場合は解析する必要なし。
						buf = ByteBuffer.allocate(ch.size() - ch.position());
						continue;
					}
					return setupFrame(buf);
				}
				else if(firstByte == 0) {
					secondByte = BufferUtil.safeRead(ch, 1).get();
					if(secondByte == 1) {
						checkLastData(buf, lastData);
						buf.flip();
						if(buf.remaining() == 0) {
							// データがない場合は解析する必要なし。
							buf = ByteBuffer.allocate(ch.size() - ch.position());
							continue;
						}
						return setupFrame(buf);
					}
					else {
						// nal分岐ではなかったその１
						if(lastData != null) {
							buf.putShort(lastData);
						}
						buf.putShort(data);
						buf.put(firstByte);
						buf.put(secondByte);
					}
				}
				else {
					// nal分岐ではなかった、その２
					if(lastData != null) {
						buf.putShort(lastData);
					}
					buf.putShort(data);
					buf.put(firstByte);
				}
				lastData = null;
			}
			else {
				if(lastData != null && data == 1 && (lastData & 0x00FF) == 0) {
					checkLastData(buf, lastData);
					buf.flip();
					if(buf.remaining() == 0) {
						// データがない場合は解析する必要なし。
						buf = ByteBuffer.allocate(ch.size() - ch.position());
						continue;
					}
					return setupFrame(buf);
				}
				setLastData(buf, lastData);
				lastData = data;
			}
		}
		// 最後まで読み込めた
		setLastData(buf, lastData);
		if(ch.size() - ch.position() == 1) {
			buf.put(BufferUtil.safeRead(ch, 1).get());
		}
		buf.flip();
		if(buf.remaining() == 0) {
			return null;
		}
		return setupFrame(buf);
	}
	private Frame setupFrame(ByteBuffer buffer) throws Exception {
		int size = buffer.remaining();
		IReadChannel ch = new ByteReadChannel(buffer);
		Frame frame = super.analyze(ch);
		frame.setSize(size);
		frame.analyze(ch);
		return frame;
	}
	private void setLastData(ByteBuffer buf, Short lastData) {
		if(lastData != null) {
			buf.putShort(lastData);
		}
	}
	/**
	 * 終端データを調整する。
	 * @param buf
	 * @param lastData
	 */
	private void checkLastData(ByteBuffer buf, Short lastData) {
		if(lastData != null) {
			if((lastData & 0x00FF) == 0) {
				//lastData >>> 8; // 前のnalデータに追記すべきデータ
				buf.put((byte)(lastData >>> 8));
			}
			else {
				buf.putShort(lastData);
			}
		}
	}
}
