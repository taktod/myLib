package com.ttProject.frame.h264;

import org.apache.log4j.Logger;

import com.ttProject.frame.VideoSelector;
import com.ttProject.frame.h264.type.AccessUnitDelimiter;
import com.ttProject.frame.h264.type.PictureParameterSet;
import com.ttProject.frame.h264.type.SequenceParameterSet;
import com.ttProject.frame.h264.type.Slice;
import com.ttProject.frame.h264.type.SliceIDR;
import com.ttProject.frame.h264.type.SupplementalEnhancementInformation;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.IUnit;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit2;
import com.ttProject.unit.extra.bit.Bit5;

/**
 * h264のframeを選択します
 * @author taktod
 */
public class H264FrameSelector extends VideoSelector {
	/** ロガー */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(H264FrameSelector.class);
	/** 解析sps */
	private SequenceParameterSet sps = null;
	/** 解析pps */
	private PictureParameterSet  pps = null;
	/** 複数のnalの応答として */
	private SliceFrame sliceFrame = null;
	/**
	 * {@inheritDoc}
	 */
	@Override
	public IUnit select(IReadChannel channel) throws Exception {
		BitLoader loader = new BitLoader(channel);
		Bit1 forbiddenZeroBit = new Bit1();
		Bit2 nalRefIdc = new Bit2();
		Bit5 type = new Bit5();
		loader.load(forbiddenZeroBit, nalRefIdc, type);
		H264Frame frame = null;
		switch(Type.getType(type.get())) {
		case AccessUnitDelimiter:
			frame = new AccessUnitDelimiter(forbiddenZeroBit, nalRefIdc, type);
			break;
		case PictureParameterSet:
			frame = new PictureParameterSet(forbiddenZeroBit, nalRefIdc, type);
			pps = (PictureParameterSet)frame;
			break;
		case SequenceParameterSet:
			frame = new SequenceParameterSet(forbiddenZeroBit, nalRefIdc, type);
			sps = (SequenceParameterSet)frame;
			break;
		case Slice: // innerFrame
			frame = new Slice(forbiddenZeroBit, nalRefIdc, type);
			break;
		case SliceIDR: // keyFrame
			frame = new SliceIDR(forbiddenZeroBit, nalRefIdc, type);
			break;
		case SupplementalEnhancementInformation:
			frame = new SupplementalEnhancementInformation(forbiddenZeroBit, nalRefIdc, type);
			break;
		default:
			throw new Exception("想定外のフレームを検知しました。");
		}
		setup(frame);
		if(!(frame instanceof SequenceParameterSet)) {
			frame.setSps(sps);
			if(!(frame instanceof PictureParameterSet)) {
				frame.setPps(pps);
			}
		}
		frame.minimumLoad(channel);
		return frame;
	}
}
