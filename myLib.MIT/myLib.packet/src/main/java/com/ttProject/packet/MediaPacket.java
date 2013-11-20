package com.ttProject.packet;

import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;

/**
 * MediaPacket動作の共通動作部分抜きだし
 * @author taktod
 */
public abstract class MediaPacket implements IMediaPacket {
	/** 保持データ実体 */
	private ByteBuffer buffer;
	private float duration = 0;
	/**
	 * 書き込み用のバッファ参照
	 * TODO この方法だと、bufferが大きくなると動作が重くなります。
	 * @param size 必要サイズ
	 * @return
	 */
	protected ByteBuffer getBuffer(int size) {
		if(buffer == null) { // なかったら新規作成
			buffer = ByteBuffer.allocate(size + 65536);
		}
		if(buffer.remaining() >= size) { // 容量の残りが必要量ある場合はそのまま応答
			return buffer;
		}
		// 必要量ないので、新規にバッファを再生成
		ByteBuffer newBuffer = ByteBuffer.allocate(buffer.capacity() + size + 65536); 
		buffer.flip();
		newBuffer.put(buffer);
		buffer = newBuffer;
		return buffer;
	}
	/**
	 * すでに保持しているバッファサイズを参照する。
	 * @return 持っているデータ量
	 */
	protected int getBufferSize() {
		if(buffer == null) {
			return 0;
		}
		return buffer.position();
	}
	/**
	 * ファイルにデータを書き込む動作
	 */
	@Override
	public void writeData(String targetFile, boolean append) {
		try {
			WritableByteChannel channel = Channels.newChannel(new FileOutputStream(targetFile, append));
			buffer.flip();
			channel.write(buffer);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * パケットの長さ設定
	 * @param duration
	 */
	protected void setDuration(float duration) {
		this.duration = duration;
	}
	/**
	 * パケットの長さ取得
	 */
	@Override
	public float getDuration() {
		return duration;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public byte[] getRawData() {
		buffer.flip();
		byte[] data = new byte[buffer.limit()];
		buffer.get(data);
		return data;
	}
}
