package com.ttProject.media.h264.frame;

import org.apache.log4j.Logger;

import com.ttProject.media.extra.Bit1;
import com.ttProject.media.extra.Bit2;
import com.ttProject.media.extra.Bit8;
import com.ttProject.media.extra.BitLoader;
import com.ttProject.media.extra.Seg;
import com.ttProject.media.extra.Ueg;
import com.ttProject.media.h264.Frame;
import com.ttProject.media.h264.IFrameAnalyzer;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;

/**
 * SequenceParameterSet
 * profileとかlevelとか、その他の細かい設定とかがはいっているみたい。
 * 
 * 67 64 00 15 ac c8 60 20 09 6c 04 40 00 00 03 00 40 00 00 07 a3 c5 8b 67 80
 * 
 * width = ((pic_width_in_mbs_minus1 +1)*16) - frame_crop_left_offset*2 - frame_crop_right_offset*2;
 * height= ((2 - frame_mbs_only_flag)* (pic_height_in_map_units_minus1 +1) * 16) - (frame_crop_top_offset * 2) - (frame_crop_bottom_offset * 2);
 * 
 * ((31 + 1) * 16) - 0 * 2 - 0 * 2 = 512 
 * ((2 - 1) * (17 + 1) * 16) - 0 * 2 - 0 * 2 = 288
 * 
 * 67 4D 40 1E  92 42 01 40 5F F2 E0 22 00 00 03 00 C8 00 00 2E D5 1E 2C 5C 90
 * 
 * あってますね。640x380とれました。
 * 
 * @see http://stackoverflow.com/questions/6394874/fetching-the-dimensions-of-a-h264video-stream
 * 
 * @author taktod
 */
public class SequenceParameterSet extends Frame {
	/** ロガー */
	private Logger logger = Logger.getLogger(SequenceParameterSet.class);
	// 先頭の３バイトからこのデータが取得可能
	private Bit8 profileIdc;
	private Bit1 constraintSet0Flag;
	private Bit1 constraintSet1Flag;
	private Bit1 constraintSet2Flag;
	private Bit1 constraintSet3Flag;
	private Bit1 constraintSet4Flag;
	private Bit1 constraintSet5Flag;
	private Bit2 reservedZeroBits;
	private Bit8 levelIdc;
	
	private Ueg seqParameterSetId;
	private Ueg chromaFormatIdc;
	private Bit1 separateColourPlaneFlag;
	private Ueg bitDepthLumaMinus8;
	private Ueg bitDepthChromaMinus8;
	private Bit1 qpprimeYZeroTransformBypassFlag;
	private Bit1 seqScalingMatrixPresentFlag;
	private Bit1[] seqScalingListPresentFlag;
	
	private Ueg log2MaxFrameNumMinus4;
	private Ueg picOrderCntType;
	private Ueg log2MaxPicOrderCntLsbMinus4;
	private Bit1 deltaPicOrderAlwaysZeroFlag;
	private Seg offsetForNonRefPic;
	private Seg offsetForTopToBottomField;
	private Ueg numRefFramesInPicOrderCntCycle;
	private Seg[] offsetForRefFrame;
	private Ueg maxNumRefFrames;
	private Bit1 gapsInFrameNumValueAllowedFlag;
	private Ueg picWidthInMbsMinus1;
	private Ueg picHeightInMapUnitsMinus1;
	private Bit1 frameMbsOnlyFlag;
	private Bit1 mbAdaptiveFrameFieldFlag;
	private Bit1 direct8x8InferenceFlag;
	private Bit1 frameCroppingFlag;
	private Ueg frameCropLeftOffset;
	private Ueg frameCropRightOffset;
	private Ueg frameCropTopOffset;
	private Ueg frameCropBottomOffset;
	private Bit1 vuiParametersPresentFlag;
	// 
	public SequenceParameterSet(int size, byte frameTypeData) {
		super(size, frameTypeData);
	}
	public SequenceParameterSet(byte frameTypeData) {
		this(0, frameTypeData);
	}
	@Override
	public void analyze(IReadChannel ch, IFrameAnalyzer analyzer)
			throws Exception {
		super.analyze(ch, analyzer);
		BitLoader bitLoader = new BitLoader(new ByteReadChannel(getBuffer()));
		profileIdc = new Bit8();
		constraintSet0Flag = new Bit1();
		constraintSet1Flag = new Bit1();
		constraintSet2Flag = new Bit1();
		constraintSet3Flag = new Bit1();
		constraintSet4Flag = new Bit1();
		constraintSet5Flag = new Bit1();
		reservedZeroBits = new Bit2();
		levelIdc = new Bit8();
		seqParameterSetId = new Ueg();
		bitLoader.load(profileIdc,
				constraintSet0Flag,
				constraintSet1Flag,
				constraintSet2Flag,
				constraintSet3Flag,
				constraintSet4Flag,
				constraintSet5Flag,
				reservedZeroBits,
				levelIdc,
				seqParameterSetId);
		int profile = profileIdc.get();
		if(profile == 100 || profile == 110 ||
				profile == 122 || profile == 244 || profile == 44 ||
				profile == 83 || profile == 86 || profile == 118 ||
				profile == 128 || profile == 138) {
			chromaFormatIdc = new Ueg();
			bitLoader.load(chromaFormatIdc);
			if(chromaFormatIdc.getData() == 3) {
				separateColourPlaneFlag = new Bit1();
				bitLoader.load(separateColourPlaneFlag);
			}
			bitDepthLumaMinus8 = new Ueg();
			bitDepthChromaMinus8 = new Ueg();
			qpprimeYZeroTransformBypassFlag = new Bit1();
			seqScalingMatrixPresentFlag = new Bit1();
			bitLoader.load(bitDepthLumaMinus8,
					bitDepthChromaMinus8,
					qpprimeYZeroTransformBypassFlag,
					seqScalingMatrixPresentFlag);
			if(seqScalingMatrixPresentFlag.get() == 1) {
				throw new Exception("seqScalingMatrixの解析動作は未実装です。");
			}
		}
		log2MaxFrameNumMinus4 = new Ueg();
		picOrderCntType = new Ueg();
		bitLoader.load(log2MaxFrameNumMinus4,
				picOrderCntType);
		if(picOrderCntType.getData() == 0) {
			log2MaxPicOrderCntLsbMinus4 = new Ueg();
			bitLoader.load(log2MaxPicOrderCntLsbMinus4);
		}
		else if(picOrderCntType.getData() == 1) {
			deltaPicOrderAlwaysZeroFlag = new Bit1();
			offsetForNonRefPic = new Seg();
			offsetForTopToBottomField = new Seg();
			numRefFramesInPicOrderCntCycle = new Ueg();
			bitLoader.load(deltaPicOrderAlwaysZeroFlag,
					offsetForNonRefPic,
					offsetForTopToBottomField,
					numRefFramesInPicOrderCntCycle);
			int cnt = numRefFramesInPicOrderCntCycle.getData();
			offsetForRefFrame = new Seg[cnt];
			for(int i = 0;i < cnt;i ++) {
				offsetForRefFrame[i] = new Seg();
				bitLoader.load(offsetForRefFrame[i]);
			}
		}
		maxNumRefFrames = new Ueg();
		gapsInFrameNumValueAllowedFlag = new Bit1();
		picWidthInMbsMinus1 = new Ueg();
		picHeightInMapUnitsMinus1 = new Ueg();
		frameMbsOnlyFlag = new Bit1();
		bitLoader.load(maxNumRefFrames,
				gapsInFrameNumValueAllowedFlag,
				picWidthInMbsMinus1,
				picHeightInMapUnitsMinus1,
				frameMbsOnlyFlag);
		if(frameMbsOnlyFlag.get() == 0) {
			mbAdaptiveFrameFieldFlag = new Bit1();
			bitLoader.load(mbAdaptiveFrameFieldFlag);
		}
		direct8x8InferenceFlag = new Bit1();
		frameCroppingFlag = new Bit1();
		bitLoader.load(direct8x8InferenceFlag,
				frameCroppingFlag);
		if(frameCroppingFlag.get() == 1) {
			frameCropLeftOffset = new Ueg();
			frameCropRightOffset = new Ueg();
			frameCropTopOffset = new Ueg();
			frameCropBottomOffset = new Ueg();
			bitLoader.load(frameCropLeftOffset,
					frameCropRightOffset,
					frameCropTopOffset,
					frameCropBottomOffset);
		}
		vuiParametersPresentFlag = new Bit1();
		bitLoader.load(vuiParametersPresentFlag);
		if(vuiParametersPresentFlag.get() == 1) {
			// parameterを読み込む
		}
		int width, height;
		width = (picWidthInMbsMinus1.getData() + 1) * 16;
		height = ((2 - frameMbsOnlyFlag.get()) * (picHeightInMapUnitsMinus1.getData() + 1) * 16);
		if(frameCroppingFlag.get() == 1) {
			width = width - frameCropLeftOffset.getData() * 2 - frameCropRightOffset.getData() * 2;
			height = height - frameCropTopOffset.getData() * 2 - frameCropBottomOffset.getData() * 2;
		}
		logger.info("width:" + width);
		logger.info("height:" + height);
	}
}
