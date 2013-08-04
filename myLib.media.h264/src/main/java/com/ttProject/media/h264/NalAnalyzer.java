package com.ttProject.media.h264;

import java.nio.ByteBuffer;

import com.ttProject.nio.CacheBuffer;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.util.HexUtil;

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
		// とりあえず前からデータをみていって、00 00 01もしくは00 00 00 01がきたら部ちぎる必要があるみたいです。
		CacheBuffer buffer = new CacheBuffer(ch);
		// 00 00 01で切って、00 00 01になるまで読み込んでおく。
		// 読み込みはじめに00 00 01以外がくる場合は、そこからframeがはじまると解釈すればよさそう。
		Short lastData = null;
		ByteBuffer buf = ByteBuffer.allocate(buffer.remaining());
		// データを読み込んでいく。
		while(buffer.remaining() > 1) {
			// shortで確認した方がいい可能性もある。
			short data = buffer.getShort();
			// 00 00 00 01もしくは 00 00 01がnalの分岐点
			// よってshort = 0になった場合に注意して2バイト先までbyteでデータを取得すればよさそう。
			if(data == 0) {
				// 前のデータを確認した、下の部分が00だったら追加するデータがアレになる。
				byte firstByte, secondByte;
				firstByte = buffer.get();
				if(firstByte == 1) {
					if(lastData != null) {
						if((lastData & 0x00FF) == 0) {
							//lastData >>> 8; // 前のnalデータに追記すべきデータ
							buf.put((byte)(lastData >>> 8));
						}
						else {
							buf.putShort(lastData);
						}
					}
					buf.flip();
					System.out.println(HexUtil.toHex(buf));
					buf = ByteBuffer.allocate(buffer.remaining());
					// nal構造の分岐点取得
					System.out.println("nal分岐");
					lastData = null;
					continue;
				}
				else if(firstByte == 0) {
					secondByte = buffer.get();
					if(secondByte == 1) {
						if(lastData != null) {
							if((lastData & 0x00FF) == 0) {
								//lastData >>> 8; // 前のnalデータに追記すべきデータ
								buf.put((byte)(lastData >>> 8));
							}
							else {
								buf.putShort(lastData);
							}
						}
						buf.flip();
						System.out.println(HexUtil.toHex(buf));
						buf = ByteBuffer.allocate(buffer.remaining());
						// nal構造の分岐点取得
						System.out.println("nal分岐2");
						lastData = null;
						continue;
					}
					else {
						// nal分岐ではなかったその１
						buf.putShort(lastData);
						buf.putShort(data);
						buf.put(firstByte);
						buf.put(secondByte);
						lastData = null;
						continue;
					}
				}
				else {
					// nal分岐ではなかった、その２
					buf.putShort(lastData);
					buf.putShort(data);
					buf.put(firstByte);
					lastData = null;
					continue;
				}
			}
			else {
				if(lastData != null) {
					// lastData追記すべきデータ
					buf.putShort(lastData);
				}
			}
			lastData = data;
		}
		// 最後まで読み込めた
		if(lastData != null) {
			buf.putShort(lastData);
		}
		if(buffer.remaining() == 1) {
			buf.put(buffer.get());
		}
		buf.flip();
		System.out.println(HexUtil.toHex(buf));
		System.out.println("最後まで読み込んで取得した。");
		return null;
	}
}
