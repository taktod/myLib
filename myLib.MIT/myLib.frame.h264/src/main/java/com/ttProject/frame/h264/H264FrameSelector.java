package com.ttProject.frame.h264;

import com.ttProject.frame.h264.type.AccessUnitDelimiter;
import com.ttProject.frame.h264.type.PictureParameterSet;
import com.ttProject.frame.h264.type.SequenceParameterSet;
import com.ttProject.frame.h264.type.Slice;
import com.ttProject.frame.h264.type.SliceIDR;
import com.ttProject.frame.h264.type.SupplementalEnhancementInformation;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.ISelector;
import com.ttProject.unit.IUnit;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit2;
import com.ttProject.unit.extra.bit.Bit5;

/**
 * h264のframeを選択します
 * @author taktod
 */
public class H264FrameSelector implements ISelector {
	private SequenceParameterSet sps = null;
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
			frame.setSps(sps);
			break;
		case PictureParameterSet:
			frame = new PictureParameterSet(forbiddenZeroBit, nalRefIdc, type);
			frame.setSps(sps);
			break;
		case SequenceParameterSet:
			frame = new SequenceParameterSet(forbiddenZeroBit, nalRefIdc, type);
			sps = (SequenceParameterSet)frame;
			break;
		case Slice: // innerFrame
			frame = new Slice(forbiddenZeroBit, nalRefIdc, type);
			frame.setSps(sps);
			break;
		case SliceIDR: // keyFrame
			frame = new SliceIDR(forbiddenZeroBit, nalRefIdc, type);
			frame.setSps(sps);
			break;
		case SupplementalEnhancementInformation:
			frame = new SupplementalEnhancementInformation(forbiddenZeroBit, nalRefIdc, type);
			frame.setSps(sps);
			break;
		default:
			throw new Exception("想定外のフレームを検知しました。");
		}
		frame.minimumLoad(channel);
		return frame;
	}
}
