package com.ttProject.frame.h264;

import com.ttProject.frame.h264.type.AccessUnitDelimiter;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.ISelector;
import com.ttProject.unit.IUnit;
import com.ttProject.unit.extra.Bit1;
import com.ttProject.unit.extra.Bit2;
import com.ttProject.unit.extra.Bit5;
import com.ttProject.unit.extra.BitLoader;

/**
 * h264のframeを選択します
 * @author taktod
 */
public class H264FrameSelector implements ISelector {
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
		case SequenceParameterSet:
		case Slice: // innerFrame
		case SliceIDR: // keyFrame
			break;
		default:
			throw new Exception("想定外のフレームを検知しました。");
		}
		return frame;
	}
}
