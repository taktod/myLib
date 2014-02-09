package com.ttProject.container.mkv;

import java.nio.charset.Charset;

import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.EbmlValue;
import com.ttProject.util.BufferUtil;

/**
 * 文字列を保持しているtagの動作
 * @author taktod
 */
public abstract class MkvUtf8Tag extends MkvTag{
	private String value;
	/**
	 * コンストラクタ
	 * @param id
	 * @param size
	 */
	public MkvUtf8Tag(Type id, EbmlValue size) {
		super(id, size);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(IReadChannel channel) throws Exception {
		value = new String(BufferUtil.safeRead(channel, getMkvSize()).array(), Charset.forName("Utf8")).intern();
		super.load(channel);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString(String space) {
		StringBuilder data = new StringBuilder();
		data.append(super.toString(space));
		data.append(" string:").append(value);
		return data.toString();
	}
}
