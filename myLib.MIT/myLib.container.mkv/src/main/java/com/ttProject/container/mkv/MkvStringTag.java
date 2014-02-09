package com.ttProject.container.mkv;

import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.EbmlValue;
import com.ttProject.util.BufferUtil;

/**
 * 文字列を保持しているtagの動作
 * @author taktod
 */
public abstract class MkvStringTag extends MkvTag{
	private String value = null;
	/**
	 * コンストラクタ
	 * @param id
	 * @param size
	 */
	public MkvStringTag(Type id, EbmlValue size) {
		super(id, size);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(IReadChannel channel) throws Exception {
		value = new String(BufferUtil.safeRead(channel, getMkvSize()).array()).intern();
		super.load(channel);
	}
	/**
	 * 保持文字列参照
	 */
	public String getValue() {
		return value;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder data = new StringBuilder();
		data.append("class:").append(getClass().getSimpleName());
		data.append(" size:").append(Integer.toHexString(getMkvSize()));
		data.append(" string:").append(value);
		return data.toString();
	}
}
