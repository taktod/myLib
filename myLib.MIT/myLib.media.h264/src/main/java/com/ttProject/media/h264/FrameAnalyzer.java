package com.ttProject.media.h264;

import com.ttProject.media.h264.frame.PictureParameterSet;
import com.ttProject.media.h264.frame.SequenceParameterSet;
import com.ttProject.media.h264.frame.Slice;
import com.ttProject.media.h264.frame.SliceIDR;
import com.ttProject.media.h264.frame.SupplementalEnhancementInformation;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.util.BufferUtil;

/**
 * frameの内容を解析する動作
 * mpegtsみたいなnalは00 00 01 フレームとなっています。
 * flvやmp4みたいなやつは[4バイトサイズ] フレームとなっています。
 * そのフレームの部分を読み込む動作
 * @author taktod
 *
 */
public class FrameAnalyzer implements IFrameAnalyzer {
	private SequenceParameterSet lastSps = null;
	public void setLastSps(SequenceParameterSet sps) {
		this.lastSps = sps;
	}
	@Override
	public Frame analyze(IReadChannel ch) throws Exception {
		// 1バイト読み込んでフレームのタイプがなにであるか知る必要がある。
		byte frameTypeData = BufferUtil.safeRead(ch, 1).get();
		Type type = Type.getType(frameTypeData & 0x1F);
		Frame frame = null;
		switch(type) {
		case Slice:
			frame = new Slice(frameTypeData);
			break;
		case SliceIDR:
			frame = new SliceIDR(frameTypeData);
			break;
		case SequenceParameterSet:
			frame = new SequenceParameterSet(frameTypeData);
			lastSps = (SequenceParameterSet)frame;
			return frame;
		case PictureParameterSet:
			frame = new PictureParameterSet(frameTypeData);
			break;
		case SupplementalEnhancementInformation:
			frame = new SupplementalEnhancementInformation(frameTypeData);
			break;
//		case AccessUnitDelimiter:
//			break;
		default:
			throw new Exception("しらないデータタイプをうけとりました。:" + type);
		}
		frame.setSps(lastSps);
		return frame;
	}
}
