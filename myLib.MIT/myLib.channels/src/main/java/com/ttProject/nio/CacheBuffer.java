package com.ttProject.nio;

import java.nio.ByteBuffer;

import com.ttProject.nio.channels.IReadChannel;

/**
 * ファイル上のデータにより高速にアクセスするためのバッファ
 * なるべくbufferからデータを取り出すことで高速アクセスを実現してみる。
 * @author taktod
 */
public class CacheBuffer {
	/** 動作用buffer */
	private ByteBuffer buffer = null;
	/** 動作ターゲットチャンネル */
	private IReadChannel targetChannel;
	/** 処理位置 */
	private int position;
	/** 残り読み込みデータ量forChannel */
	private int remaining; // 残り読み込みデータ量
	/**
	 * コンストラクタ
	 * @param source
	 * @throws Exception
	 */
	public CacheBuffer(IReadChannel source) throws Exception {
		this(source, source.size() - source.position());
	}
	/**
	 * コンストラクタ
	 * @param source
	 * @param size
	 * @throws Exception
	 */
	public CacheBuffer(IReadChannel source, int size) throws Exception {
		this.targetChannel = source;
		this.position = source.position();
		this.remaining = size;
	}
	public byte get() throws Exception {
		resetData(1);
		return buffer.get();
	}
	public short getShort() throws Exception {
		resetData(2);
		return buffer.getShort();
	}
	public long getLong() throws Exception {
		resetData(8);
		return buffer.getLong();
	}
	/**
	 * 整数の値のみ応答することにします。
	 * @return
	 */
	public int getInt() throws Exception {
		resetData(4);
		return buffer.getInt();
	}
	/**
	 * 3バイト読み込む
	 * @return
	 * @throws Exception
	 */
	public int getMidiumInt() throws Exception {
		// buffer内のデータがまにあっているか確認する。
		resetData(3);
		return (buffer.get() << 16) + buffer.getShort();
	}
	/**
	 * 任意のデータ量読み込む
	 * @param size
	 * @return
	 * @throws Exception
	 */
	public ByteBuffer getBuffer(int size) throws Exception {
		resetData(size);
		byte[] data = new byte[size];
		buffer.get(data);
		return ByteBuffer.wrap(data);
	}
	/**
	 * データが足りない場合に、データの読み直しを実施してみる。
	 * @throws Exception
	 */
	private void resetData(int bytesLoad) throws Exception {
		// buffer内のデータがまにあっているか確認する。
		if(buffer == null || buffer.remaining() < bytesLoad) {
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
		if(buffer.remaining() < bytesLoad) {
			throw new Exception("データがたりません。");
		}
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
	/**
	 * 読み込み位置を応答する。
	 * @return
	 */
	public int position() {
		return position - buffer.remaining();
	}
}
