package com.ttProject.container.mp4;

import com.ttProject.container.Container;
import com.ttProject.container.Reader;
import com.ttProject.nio.channels.IReadChannel;
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
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		setPosition(channel.position() - 8);
	}
	@Override
	public void load(IReadChannel channel) throws Exception {
		channel.position(getPosition() + getSize());
	}
	public void setMp4AtomReader(Mp4AtomReader reader) {
		this.reader = reader;
	}
	protected Reader getMp4AtomReader() {
		return reader;
	}
	@Override
	public String toString() {
		StringBuilder data = new StringBuilder();
		data.append(getClass().getSimpleName());
		data.append(" pos:").append(getPosition());
		data.append(" size:").append(getSize());
		return data.toString();
	}
}
