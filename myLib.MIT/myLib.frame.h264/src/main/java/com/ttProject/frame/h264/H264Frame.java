package com.ttProject.frame.h264;

import java.nio.ByteBuffer;
import java.util.List;

import com.ttProject.frame.VideoFrame;
import com.ttProject.frame.h264.type.PictureParameterSet;
import com.ttProject.frame.h264.type.SequenceParameterSet;
import com.ttProject.unit.extra.BitConnector;
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit2;
import com.ttProject.unit.extra.bit.Bit5;

/**
 * h264のframe
 * @author taktod
 */
public abstract class H264Frame extends VideoFrame {
	private final Bit1 forbiddenZeroBit;
	private final Bit2 nalRefIdc;
	private final Bit5 type;

	/** 解析済みsps */
	private SequenceParameterSet sps = null;
	/** 解析済みpps */
	private PictureParameterSet pps = null;
	/** 複数フレームで同一データとする場合のフレームリスト */
	private List<H264Frame> frameList = null;
	/**
	 * コンストラクタ
	 * @param forbiddenZeroBit
	 * @param nalRefIdc
	 * @param type
	 */
	public H264Frame(Bit1 forbiddenZeroBit, Bit2 nalRefIdc, Bit5 type) {
		this.forbiddenZeroBit = forbiddenZeroBit;
		this.nalRefIdc = nalRefIdc;
		this.type = type;
	}
	/**
	 * 先頭のtypeBufferを参照する
	 * @return
	 */
	protected ByteBuffer getTypeBuffer() {
		BitConnector connector = new BitConnector();
		return connector.connect(forbiddenZeroBit,
				nalRefIdc, type);
	}
	/**
	 * pps設定
	 * @param pps
	 */
	public void setPps(PictureParameterSet pps) {
		this.pps = pps;
	}
	/**
	 * sps設定
	 * @param sps
	 */
	public void setSps(SequenceParameterSet sps) {
		this.sps = sps;
		if(sps != null) {
			setWidth(sps.getWidth());
			setHeight(sps.getHeight());
		}
	}
	/**
	 * sps参照
	 * @return
	 */
	public SequenceParameterSet getSps() {
		return sps;
	}
	/**
	 * pps参照
	 * @return
	 */
	public PictureParameterSet getPps() {
		return pps;
	}
	public List<H264Frame> getGroupNalList() {
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
