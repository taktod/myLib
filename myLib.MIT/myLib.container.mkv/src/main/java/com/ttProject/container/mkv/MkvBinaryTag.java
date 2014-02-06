package com.ttProject.container.mkv;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.EbmlValue;
import com.ttProject.util.BufferUtil;

/**
 * Binaryデータを保持するTagの動作
 * @author taktod
 */
public abstract class MkvBinaryTag extends MkvTag {
	/** ロガー */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(MkvBinaryTag.class);
	private ByteBuffer buffer = null;
	/**
	 * コンストラクタ
	 * @param id
	 * @param size
	 */
	public MkvBinaryTag(Type id, EbmlValue size) {
		super(id, size);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(IReadChannel channel) throws Exception {
		buffer = BufferUtil.safeRead(channel, getMkvSize());
		super.load(channel);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder data = new StringBuilder();
		data.append("class:").append(getClass().getSimpleName());
		data.append(" size:").append(Integer.toHexString(getMkvSize()));
		if(buffer == null) {
			data.append(" binary:").append("null");
		}
		else {
			data.append(" binary:").append(buffer.remaining());
		}
		return data.toString();
	}
}
