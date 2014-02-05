package com.ttProject.frame.h264.type;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.ttProject.frame.h264.H264Frame;
import com.ttProject.frame.h264.SliceFrame;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.BitConnector;
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit2;
import com.ttProject.unit.extra.bit.Bit24;
import com.ttProject.unit.extra.bit.Bit5;
import com.ttProject.unit.extra.bit.Bit8;
import com.ttProject.util.BufferUtil;

/**
 * Slice
 * h264の基本となる中間フレーム
 * @author taktod
 */
public class Slice extends SliceFrame {
	/** データ */
	private ByteBuffer buffer = null;
	/**
	 * コンストラクタ
	 * @param forbiddenZeroBit
	 * @param nalRefIdc
	 * @param type
	 */
	public Slice(Bit1 forbiddenZeroBit, Bit2 nalRefIdc, Bit5 type) {
		super(forbiddenZeroBit, nalRefIdc, type);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		super.minimumLoad(channel);
		setReadPosition(channel.position());
		setSize(channel.size());
		super.update();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(IReadChannel channel) throws Exception {
		channel.position(getReadPosition());
		buffer = BufferUtil.safeRead(channel, getSize() - channel.position());
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
				getSliceHeaderBuffer(),
				buffer));
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ByteBuffer getPackBuffer() throws Exception {
		BitConnector connector = new BitConnector();
		List<ByteBuffer> bufferList = new ArrayList<ByteBuffer>();
		// sliceFrame
		bufferList.add(connector.connect(new Bit8()));
		for(H264Frame frame : getGroupFrameList()) {
			if(frame instanceof Slice) {
				bufferList.add(connector.connect(new Bit24(1)));
				bufferList.add(frame.getData());
			}
			else {
				throw new Exception("想定外のframeが含まれていました。:" + getClass());
			}
		}
		return BufferUtil.connect(bufferList);
	}
}
