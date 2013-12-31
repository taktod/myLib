package com.ttProject.frame.h264.type;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import com.ttProject.frame.h264.H264Frame;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit2;
import com.ttProject.unit.extra.bit.Bit5;
import com.ttProject.util.BufferUtil;

/**
 * accessUnitDelimiter
 * mpegtsで各frameの頭にはいっている仕切りみたいなもの
 * @author taktod
 */
public class AccessUnitDelimiter extends H264Frame {
	/** ロガー */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(AccessUnitDelimiter.class);
	/** データ実体 */
	private ByteBuffer buffer = null;
	/**
	 * コンストラクタ
	 * @param forbiddenZeroBit
	 * @param nalRefIdc
	 * @param type
	 */
	public AccessUnitDelimiter(Bit1 forbiddenZeroBit, Bit2 nalRefIdc, Bit5 type) {
		super(forbiddenZeroBit, nalRefIdc, type);
		super.update();
	}
	/**
	 * コンストラクタ
	 */
	public AccessUnitDelimiter() {
		super(new Bit1(), new Bit2(), new Bit5(0x09));
		buffer = ByteBuffer.allocate(1);
		buffer.put((byte)0xF0);
		buffer.flip();
		super.setSize(2);
		super.update();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		setReadPosition(channel.position());
		setSize(channel.size());
		super.update();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(IReadChannel channel) throws Exception {
		buffer = BufferUtil.safeRead(channel, getSize() - getReadPosition());
		super.update();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
		if(buffer == null) {
			throw new Exception("データ実体が読み込まれていません");
		}
		setData(BufferUtil.connect(getTypeBuffer(),
				buffer));
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ByteBuffer getPackBuffer() throws Exception {
		return null;
	}
}
