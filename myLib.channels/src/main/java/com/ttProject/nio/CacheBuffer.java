package com.ttProject.nio;

import java.nio.ByteBuffer;

import com.ttProject.nio.channels.IFileReadChannel;

/**
 * ファイル上のデータにより高速にアクセスするためのバッファ
 * なるべくbufferからデータを取り出すことで高速アクセスを実現してみる。
 * TODO そろそろintのみではなく、他のデータも引き出せるようにしたい
 * @author taktod
 */
public class CacheBuffer {
	/** 動作用buffer */
	private ByteBuffer buffer = null;
	/** 動作ターゲットチャンネル */
	private IFileReadChannel targetChannel;
	/** 処理位置 */
	private int position;
	/** 残り読み込みデータ量forChannel */
	private int remaining; // 残り読み込みデータ量
	/**
	 * コンストラクタ
	 * @param source
	 * @throws Exception
	 */
	public CacheBuffer(IFileReadChannel source) throws Exception {
		this(source, source.size());
	}
	/**
	 * コンストラクタ
	 * @param source
	 * @param size
	 * @throws Exception
	 */
	public CacheBuffer(IFileReadChannel source, int size) throws Exception {
		this.targetChannel = source;
		this.position = source.position();
		this.remaining = size;
	}
	/**
	 * 整数の値のみ応答することにします。
	 * @return
	 */
	public int getInt() throws Exception {
		// buffer内のデータがまにあっているか確認する。
		if(buffer == null || buffer.remaining() < 4) {
			// 残りデータが0だったらもうデータなし
			if(remaining == 0 && buffer.remaining() == 0) {
				throw new Exception("eof already");
			}
			// bufferの中にあるデータ量を確認
			int bufRemain;
			if(buffer == null) {
				bufRemain = 0;
			}
			else {
				bufRemain = buffer.remaining();
			}
			// bufferが足りないので読み込む必要がある。
			int bufSize = (16777216 > remaining) ? remaining : 16777216;
			ByteBuffer buf = ByteBuffer.allocate(bufSize);
			targetChannel.position(position); // 処理位置から
			targetChannel.read(buf); // 必要なデータを読み込む
			buf.flip();
			// 位置情報と残りデータ量を更新しておく。
			position += buf.remaining();
			remaining -= buf.remaining();
			// bufferデータを更新しておく。
			ByteBuffer buf2 = ByteBuffer.allocate(buf.remaining() + bufRemain);
			if(bufRemain != 0) {
				buf2.put(buffer);
			}
			buf2.put(buf);
			buf2.flip();
			buffer = buf2;
		}
		return buffer.getInt();
	}
	/**
	 * 残りデータ量を応答する
	 * @return
	 */
	public int remaining() {
		if(buffer == null) {
			return remaining;
		}
		return remaining + buffer.remaining();
	}
}
