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
	@Override
	public Frame analyze(IReadChannel ch) throws Exception {
		// 1バイト読み込んでフレームのタイプがなにであるか知る必要がある。
		byte frameTypeData = BufferUtil.safeRead(ch, 1).get();
		Type type = Type.getType(frameTypeData & 0x1F);
		switch(type) {
		case Slice:
			return new Slice(frameTypeData);
		case SliceIDR:
			return new SliceIDR(frameTypeData);
		case SequenceParameterSet:
			return new SequenceParameterSet(frameTypeData);
		case PictureParameterSet:
			return new PictureParameterSet(frameTypeData);
		case SupplementalEnhancementInformation:
			return new SupplementalEnhancementInformation(frameTypeData);
//		case AccessUnitDelimiter:
//			break;
		default:
			throw new Exception("しらないデータタイプをうけとりました。:" + type);
		}
//		return null;
	}
}
