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
 * h264のkeyFrameにあたるSliceIDR
 * @author taktod
 */
public class SliceIDR extends H264Frame {
	/** ロガー */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(SliceIDR.class);
	/** データ */
	private ByteBuffer buffer = null;
	/**
	 * コンストラクタ
	 * @param forbiddenZeroBit
	 * @param nalRefIdc
	 * @param type
	 */
	public SliceIDR(Bit1 forbiddenZeroBit, Bit2 nalRefIdc, Bit5 type) {
		super(forbiddenZeroBit, nalRefIdc, type);
		super.setKeyFrame(true);
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
		// packデータとしては、00 00 00 01 sps 00 00 00 01 pps 00 00 00 01 sliceIdrをつくればいいはず。
		ByteBuffer spsBuffer = getSps().getData();
		ByteBuffer ppsBuffer = getPps().getData();
		ByteBuffer idrBuffer = getData();
		ByteBuffer packBuffer = ByteBuffer.allocate(4 + spsBuffer.remaining() + 4 + ppsBuffer.remaining() + 4 + idrBuffer.remaining());
		packBuffer.putInt(1);
		packBuffer.put(spsBuffer);
		packBuffer.putInt(1);
		packBuffer.put(ppsBuffer);
		packBuffer.putInt(1);
		packBuffer.put(idrBuffer);
		packBuffer.flip();
		return packBuffer;
	}
}
