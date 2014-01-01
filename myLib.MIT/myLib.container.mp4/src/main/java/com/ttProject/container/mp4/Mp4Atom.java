package com.ttProject.container.mp4;

import java.nio.ByteBuffer;

import com.ttProject.container.Container;
import com.ttProject.container.Reader;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.BitConnector;
import com.ttProject.unit.extra.bit.Bit32;

/**
 * mp4Atomのベースになるクラス
 * @author taktod
 */
public abstract class Mp4Atom extends Container {
	private final Bit32 size;
	private final Bit32 name;
	private Mp4AtomReader reader = null;
	/**
	 * コンストラクタ
	 * @param length
	 * @param name
	 */
	public Mp4Atom(Bit32 size, Bit32 name) {
		this.size = size;
		this.name = name;
		super.setSize(size.get());
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		setPosition(channel.position() - 8);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(IReadChannel channel) throws Exception {
		channel.position(getPosition() + getSize());
	}
	/**
	 * mp4解析用のreaderを外部から設定します。
	 * @param reader
	 */
	public void setMp4AtomReader(Mp4AtomReader reader) {
		this.reader = reader;
	}
	/**
	 * mp4解析用のreaderを参照します
	 * @return
	 */
	protected Reader getMp4AtomReader() {
		return reader;
	}
	/**
	 * 先頭のbufferを応答します。
	 * @return
	 */
	protected ByteBuffer getHeaderBuffer() {
		BitConnector connector = new BitConnector();
		return connector.connect(size, name);
	}
	@Override
	public String toString() {
		StringBuilder data = new StringBuilder();
		data.append(getClass().getSimpleName());
		data.append(" pos:").append(Integer.toHexString(getPosition()));
		data.append(" size:").append(Integer.toHexString(getSize()));
		return data.toString();
	}
}
