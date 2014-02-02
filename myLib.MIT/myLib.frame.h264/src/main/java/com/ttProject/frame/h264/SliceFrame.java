package com.ttProject.frame.h264;

import java.nio.ByteBuffer;
import java.util.List;

import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.Bit;
import com.ttProject.unit.extra.BitConnector;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit2;
import com.ttProject.unit.extra.bit.Bit5;
import com.ttProject.unit.extra.bit.Ueg;

/**
 * slice系のデータのベースになるクラス
 * @author taktod
 */
public abstract class SliceFrame extends H264Frame {
	private Ueg firstMbInSlice        = new Ueg();
	private Ueg sliceType             = new Ueg();
	private Ueg pictureParameterSetId = new Ueg();
	private Bit extraBit              = null;
	
	/** nalグループを一手に保持しておく */
	private List<H264Frame> frameList = null;
	/**
	 * コンストラクタ
	 * @param forbiddendZeroBit
	 * @param nalRefIdc
	 * @param type
	 */
	public SliceFrame(Bit1 forbiddenZeroBit, Bit2 nalRefIdc, Bit5 type) {
		super(forbiddenZeroBit, nalRefIdc, type);
	}
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		BitLoader loader = new BitLoader(channel);
		loader.load(firstMbInSlice, sliceType, pictureParameterSetId);
		extraBit = loader.getExtraBit();
	}
	/**
	 * sliceFrameの先頭の部分のデータを応答します
	 * @return
	 */
	protected ByteBuffer getSliceHeaderBuffer() {
		BitConnector connector = new BitConnector();
		return connector.connect(firstMbInSlice, sliceType, pictureParameterSetId, extraBit);
	}
	public int getFirstMbInSlice() throws Exception {
		return firstMbInSlice.get();
	}
	public List<H264Frame> getAssociateFrame() {
		return frameList;
	}
	public boolean isFirstNal() {
		if(frameList == null) {
			return false;
		}
		if(frameList.get(0).hashCode() != this.hashCode()) {
			return false;
		}
		return true;
	}
}
