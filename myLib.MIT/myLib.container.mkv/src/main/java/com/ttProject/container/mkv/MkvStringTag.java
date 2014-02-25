package com.ttProject.container.mkv;

import java.nio.ByteBuffer;

import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.BitConnector;
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
	public void setValue(String data) {
		value = data;
		getTagSize().set(value.getBytes().length);
		super.update();
	}
	@Override
	protected void requestUpdate() throws Exception {
		if(value == null) {
			throw new Exception("値が設定されていません。");
		}
		BitConnector connector = new BitConnector();
		super.setData(BufferUtil.connect(connector.connect(getTagId(), getTagSize()), ByteBuffer.wrap(value.getBytes())));
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
