/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.h264.type;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import com.ttProject.frame.h264.H264Frame;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.Bit;
import com.ttProject.unit.extra.BitConnector;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit2;
import com.ttProject.unit.extra.bit.Bit5;
import com.ttProject.unit.extra.bit.Bit8;
import com.ttProject.unit.extra.bit.Seg;
import com.ttProject.unit.extra.bit.Ueg;
import com.ttProject.util.BufferUtil;

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
 * 67 4D 40 1E  D9 01 41 FA 10 00 00 03 00 10 00 00 7D 00 F1 62 E4 80
 * 
 * 
 * @see http://stackoverflow.com/questions/6394874/fetching-the-dimensions-of-a-h264video-stream
 * 
 * @author taktod
 */
public class SequenceParameterSet extends H264Frame {
	/** ロガー */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(SequenceParameterSet.class);
	// 先頭の３バイトからこのデータが取得可能
	private Bit8 profileIdc         = new Bit8();
	private Bit1 constraintSet0Flag = new Bit1();
	private Bit1 constraintSet1Flag = new Bit1();
	private Bit1 constraintSet2Flag = new Bit1();
	private Bit1 constraintSet3Flag = new Bit1();
	private Bit1 constraintSet4Flag = new Bit1();
	private Bit1 constraintSet5Flag = new Bit1();
	private Bit2 reservedZeroBits   = new Bit2();
	private Bit8 levelIdc           = new Bit8();
	
	private Ueg    seqParameterSetId               = new Ueg();
	private Ueg    chromaFormatIdc                 = null;
	private Bit1   separateColourPlaneFlag         = null;
	private Ueg    bitDepthLumaMinus8              = null;
	private Ueg    bitDepthChromaMinus8            = null;
	private Bit1   qpprimeYZeroTransformBypassFlag = null;
	private Bit1   seqScalingMatrixPresentFlag     = null;
	@SuppressWarnings("unused")
	private Bit1[] seqScalingListPresentFlag       = null;
	
	private Ueg   log2MaxFrameNumMinus4          = new Ueg();
	private Ueg   picOrderCntType                = new Ueg();
	private Ueg   log2MaxPicOrderCntLsbMinus4    = null;
	private Bit1  deltaPicOrderAlwaysZeroFlag    = null;
	private Seg   offsetForNonRefPic             = null;
	private Seg   offsetForTopToBottomField      = null;
	private Ueg   numRefFramesInPicOrderCntCycle = null;
	private Seg[] offsetForRefFrame              = null;

	private Ueg  maxNumRefFrames                = new Ueg();
	private Bit1 gapsInFrameNumValueAllowedFlag = new Bit1();
	private Ueg  picWidthInMbsMinus1            = new Ueg();
	private Ueg  picHeightInMapUnitsMinus1      = new Ueg();
	private Bit1 frameMbsOnlyFlag               = new Bit1();
	private Bit1 mbAdaptiveFrameFieldFlag       = null;
	private Bit1 direct8x8InferenceFlag         = new Bit1();
	private Bit1 frameCroppingFlag              = new Bit1();
	private Ueg  frameCropLeftOffset            = null;
	private Ueg  frameCropRightOffset           = null;
	private Ueg  frameCropTopOffset             = null;
	private Ueg  frameCropBottomOffset          = null;
	private Bit1 vuiParametersPresentFlag       = new Bit1();

	private Bit extraBit = null; // 超過bit
	
	private ByteBuffer buffer = null;
	
	/**
	 * コンストラクタ
	 * @param forbiddenZeroBit
	 * @param nalRefIdc
	 * @param type
	 */
	public SequenceParameterSet(Bit1 forbiddenZeroBit,
			Bit2 nalRefIdc,
			Bit5 type) {
		super(forbiddenZeroBit, nalRefIdc, type);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		BitLoader loader = new BitLoader(channel);
		loader.load(profileIdc,
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
			loader.load(chromaFormatIdc);
			if(chromaFormatIdc.get() == 3) {
				separateColourPlaneFlag = new Bit1();
				loader.load(separateColourPlaneFlag);
			}
			bitDepthLumaMinus8 = new Ueg();
			bitDepthChromaMinus8 = new Ueg();
			qpprimeYZeroTransformBypassFlag = new Bit1();
			seqScalingMatrixPresentFlag = new Bit1();
			loader.load(bitDepthLumaMinus8,
					bitDepthChromaMinus8,
					qpprimeYZeroTransformBypassFlag,
					seqScalingMatrixPresentFlag);
			if(seqScalingMatrixPresentFlag.get() == 1) {
				throw new Exception("seqScalingMatrixの解析動作は未実装です。");
			}
		}
		loader.load(log2MaxFrameNumMinus4,
				picOrderCntType);
		if(picOrderCntType.get() == 0) {
			log2MaxPicOrderCntLsbMinus4 = new Ueg();
			loader.load(log2MaxPicOrderCntLsbMinus4);
		}
		else if(picOrderCntType.get() == 1){
			deltaPicOrderAlwaysZeroFlag = new Bit1();
			offsetForNonRefPic = new Seg();
			offsetForTopToBottomField = new Seg();
			numRefFramesInPicOrderCntCycle = new Ueg();
			loader.load(deltaPicOrderAlwaysZeroFlag,
					offsetForNonRefPic,
					offsetForTopToBottomField,
					numRefFramesInPicOrderCntCycle);
			int cnt = numRefFramesInPicOrderCntCycle.get();
			offsetForRefFrame = new Seg[cnt];
			for(int i = 0;i < cnt;i ++) {
				offsetForRefFrame[i] = new Seg();
				loader.load(offsetForRefFrame[i]);
			}
		}
		loader.load(maxNumRefFrames,
				gapsInFrameNumValueAllowedFlag,
				picWidthInMbsMinus1,
				picHeightInMapUnitsMinus1,
				frameMbsOnlyFlag);
		if(frameMbsOnlyFlag.get() == 0) {
			mbAdaptiveFrameFieldFlag = new Bit1();
			loader.load(mbAdaptiveFrameFieldFlag);
		}
		loader.load(direct8x8InferenceFlag,
				frameCroppingFlag);
		if(frameCroppingFlag.get() == 1) {
			frameCropLeftOffset = new Ueg();
			frameCropRightOffset = new Ueg();
			frameCropTopOffset = new Ueg();
			frameCropBottomOffset = new Ueg();
			loader.load(frameCropLeftOffset,
					frameCropRightOffset,
					frameCropTopOffset,
					frameCropBottomOffset);
		}
		loader.load(vuiParametersPresentFlag);
		if(vuiParametersPresentFlag.get() == 1) {
			// parameterを読み込む
		}
		// 超過bitを保持しておく
		extraBit = loader.getExtraBit();
		// このタイミングで縦横データが取得可能になります。
		super.setReadPosition(channel.position());
		super.setSize(channel.size());

		int width = (picWidthInMbsMinus1.get() + 1) * 16;
		int height = ((2 - frameMbsOnlyFlag.get()) * (picHeightInMapUnitsMinus1.get() + 1) * 16);
		if(frameCroppingFlag.get() == 1) {
			width = width - frameCropLeftOffset.get() * 2 - frameCropRightOffset.get() * 2;
			height = height - frameCropTopOffset.get() * 2 - frameCropBottomOffset.get() * 2;
		}
		super.setWidth(width);
		super.setHeight(height);
		super.update();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(IReadChannel channel) throws Exception {
		channel.position(super.getReadPosition());
		buffer = BufferUtil.safeRead(channel, getSize() - getReadPosition());
		super.update();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
		if(buffer == null) {
			throw new Exception("本体データが未設定です。");
		}
		BitConnector connector = new BitConnector();
		// コネクトしていく。
		connector.feed(profileIdc, constraintSet0Flag, constraintSet1Flag,
				constraintSet2Flag, constraintSet3Flag, constraintSet4Flag,
				constraintSet5Flag, reservedZeroBits, levelIdc, seqParameterSetId,
				chromaFormatIdc, separateColourPlaneFlag, bitDepthLumaMinus8,
				bitDepthChromaMinus8, qpprimeYZeroTransformBypassFlag,
				seqScalingMatrixPresentFlag);
		connector.feed(log2MaxFrameNumMinus4, picOrderCntType, log2MaxPicOrderCntLsbMinus4,
				deltaPicOrderAlwaysZeroFlag, offsetForNonRefPic, offsetForTopToBottomField,
				numRefFramesInPicOrderCntCycle);
		if(numRefFramesInPicOrderCntCycle != null && numRefFramesInPicOrderCntCycle.get() != 0) {
			for(int i = 0;i < numRefFramesInPicOrderCntCycle.get();i ++) {
				connector.feed(offsetForRefFrame[i]);
			}
		}
		connector.feed(maxNumRefFrames, gapsInFrameNumValueAllowedFlag,
				picWidthInMbsMinus1, picHeightInMapUnitsMinus1, frameMbsOnlyFlag,
				mbAdaptiveFrameFieldFlag, direct8x8InferenceFlag, frameCroppingFlag,
				frameCropLeftOffset, frameCropRightOffset, frameCropTopOffset, frameCropBottomOffset,
				vuiParametersPresentFlag, extraBit);
		setData(BufferUtil.connect(getTypeBuffer(),
				connector.connect(),
				buffer));
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ByteBuffer getPackBuffer() {
		return null;
	}
}
