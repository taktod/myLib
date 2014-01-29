package com.ttProject.frame.h264.type;

import java.nio.ByteBuffer;

import com.ttProject.frame.h264.H264Frame;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.Bit;
import com.ttProject.unit.extra.BitConnector;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit2;
import com.ttProject.unit.extra.bit.Bit5;
import com.ttProject.unit.extra.bit.Ueg;
import com.ttProject.util.BufferUtil;

/**
 * Slice
 * h264の基本となる中間フレーム
 * @author taktod
 */
public class Slice extends H264Frame {
	private Ueg firstMbInSlice    = null;
	private Ueg sliceType         = null;
	private Ueg picParameterSetId = null;
	private Bit extraBit          = null;
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
		setReadPosition(channel.position());
		setSize(channel.size());
		super.update();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(IReadChannel channel) throws Exception {
		BitLoader loader = new BitLoader(channel);
		firstMbInSlice    = new Ueg();
		sliceType         = new Ueg();
		picParameterSetId = new Ueg();
		loader.load(firstMbInSlice, sliceType, picParameterSetId);
		extraBit = loader.getExtraBit();
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
		BitConnector connector = new BitConnector();
		setData(BufferUtil.connect(getTypeBuffer(),
				connector.connect(firstMbInSlice, sliceType, picParameterSetId, extraBit),
				buffer));
	}
	public int getFirstMbInSlice() throws Exception {
		if(firstMbInSlice == null) {
			throw new Exception("firstMbInSliceがnullでした。loadを実行してください");
		}
		return firstMbInSlice.get();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ByteBuffer getPackBuffer() throws Exception {
		ByteBuffer data = getData();
		ByteBuffer packBuffer = ByteBuffer.allocate(4 + data.remaining());
		packBuffer.putInt(1);
		packBuffer.put(data);
		packBuffer.flip();
		return packBuffer;
	}
}
