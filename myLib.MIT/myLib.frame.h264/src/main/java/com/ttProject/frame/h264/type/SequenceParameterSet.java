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
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit16;
import com.ttProject.unit.extra.bit.Bit2;
import com.ttProject.unit.extra.bit.Bit3;
import com.ttProject.unit.extra.bit.Bit32;
import com.ttProject.unit.extra.bit.Bit4;
import com.ttProject.unit.extra.bit.Bit5;
import com.ttProject.unit.extra.bit.Bit8;
import com.ttProject.unit.extra.bit.Seg;
import com.ttProject.unit.extra.bit.Ueg;
import com.ttProject.util.BufferUtil;

/**
 * SequenceParameterSet
 * hold profile, level and so on... something detail.
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
 * ok 640x380 is correct.
 * 
 * 67 4D 40 1E  D9 01 41 FA 10 00 00 03 00 10 00 00 7D 00 F1 62 E4 80
 * 
 * @see http://stackoverflow.com/questions/6394874/fetching-the-dimensions-of-a-h264video-stream
 * 
 * @author taktod
 */
public class SequenceParameterSet extends H264Frame {
	/** logger */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(SequenceParameterSet.class);
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
	
	private Bit1  aspectRatioInfoPresentFlag       = null;
	private Bit8    aspectRatioIdc                 = null;
	private Bit16     sarWidth                     = null;
	private Bit16     sarHeight                    = null;
	private Bit1  overscanInfoPresentFlag          = null;
	private Bit1    overscanAppropriateFlag        = null;
	private Bit1  videoSignalTypePresentFlag       = null;
	private Bit3    videoFormat                    = null;
	private Bit1    videoFullRangeFlag             = null;
	private Bit1    colourDescriptionPresentFlag   = null;
	private Bit8      colourPrimaries              = null;
	private Bit8      transferCharacteristics      = null;
	private Bit8      matrixCoefficients           = null;
	private Bit1  chromaLocInfoPresentFlag         = null;
	private Ueg     chromaSampleLocTypeTopField    = null;
	private Ueg     chromaSampleLocTypeBottomField = null;
	private Bit1  timingInfoPresentFlag            = null;
	private Bit32   numUnitsInTick                 = null;
	private Bit32   timeScale                      = null;
	private Bit1    fixedFrameRateFlag             = null;
	private Bit1  nalHrdParametersPresentFlag      = null;
	private Bit1  vclHrdParametersPresentFlag      = null;
	
	// hrdParameters
	private Ueg   cpbCntMinus1                       = null;
	private Bit4  bitRateScale                       = null;
	private Bit4  cpbSizeScale                       = null;
	private Ueg[]   bitRateValueMinus1               = null;
	private Ueg[]   cpbSizeValueMinus1               = null;
	private Bit1[]  cbrFlag                          = null;
	private Bit5  initialCpbRemovalDelayLengthMinus1 = null;
	private Bit5  cpbRemovalDelayLengthMinus1        = null;
	private Bit5  dpbOutputDelayLengthMinus1         = null;
	private Bit5  timeOffsetLength                   = null;
	
	private Bit1    lowDelayHrdFlag                    = null;
	private Bit1  picStructPresentFlag                 = null;
	private Bit1  bitstreamRestrictionFlag             = null;
	private Bit1    motionVectorsOverPicBoundariesFlag = null;
	private Ueg     maxBytesPerPicDenom                = null;
	private Ueg     maxBitsPerMbDenom                  = null;
	private Ueg     log2MaxMvLengthHorizontal          = null;
	private Ueg     log2MaxMvLengthVertical            = null;
	private Ueg     maxNumReorderFrames                = null;
	private Ueg     maxDecFrameBuffering               = null;
	
	private ByteBuffer buffer = null;
	
	/**
	 * constructor
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
		// have the copy of buffer.
		buffer = BufferUtil.safeRead(channel, channel.size() - channel.position());
		BitLoader loader = new BitLoader(new ByteReadChannel(buffer.duplicate()));
		loader.setEmulationPreventionFlg(true);
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
				throw new Exception("seqScalingMatrix is unexpected");
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
			// load vuiParameters
			loadVuiParameters(loader);
		}
		// now we can know the width x height
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
	 * load vuiParameter
	 */
	private void loadVuiParameters(BitLoader loader) throws Exception {
		aspectRatioInfoPresentFlag = new Bit1();
		loader.load(aspectRatioInfoPresentFlag);
		if(aspectRatioInfoPresentFlag.get() != 0) {
			aspectRatioIdc = new Bit8();
			loader.load(aspectRatioIdc);
			if(aspectRatioIdc.get() == 255) {
				sarWidth = new Bit16();
				sarHeight = new Bit16();
				loader.load(sarWidth, sarHeight);
			}
		}
		overscanInfoPresentFlag = new Bit1();
		loader.load(overscanInfoPresentFlag);
		if(overscanInfoPresentFlag.get() == 1) {
			overscanAppropriateFlag = new Bit1();
			loader.load(overscanAppropriateFlag);
		}
		videoSignalTypePresentFlag = new Bit1();
		loader.load(videoSignalTypePresentFlag);
		if(videoSignalTypePresentFlag.get() == 1) {
			videoFormat = new Bit3();
			videoFullRangeFlag = new Bit1();
			colourDescriptionPresentFlag = new Bit1();
			loader.load(videoFormat, videoFullRangeFlag, colourDescriptionPresentFlag);
			if(colourDescriptionPresentFlag.get() == 1) {
				colourPrimaries = new Bit8();
				transferCharacteristics = new Bit8();
				matrixCoefficients = new Bit8();
				loader.load(colourPrimaries, transferCharacteristics, matrixCoefficients);
			}
		}
		chromaLocInfoPresentFlag = new Bit1();
		loader.load(chromaLocInfoPresentFlag);
		if(chromaLocInfoPresentFlag.get() == 1) {
			chromaSampleLocTypeTopField = new Ueg();
			chromaSampleLocTypeBottomField = new Ueg();
			loader.load(chromaSampleLocTypeTopField, chromaSampleLocTypeBottomField);
		}
		timingInfoPresentFlag = new Bit1();
		loader.load(timingInfoPresentFlag);
		if(timingInfoPresentFlag.get() == 1) {
			numUnitsInTick = new Bit32();
			timeScale = new Bit32();
			fixedFrameRateFlag = new Bit1();
			loader.load(numUnitsInTick, timeScale, fixedFrameRateFlag);
		}
		nalHrdParametersPresentFlag = new Bit1();
		loader.load(nalHrdParametersPresentFlag);
		if(nalHrdParametersPresentFlag.get() == 1) {
			loadHrdParameters(loader);
		}
		vclHrdParametersPresentFlag = new Bit1();
		loader.load(vclHrdParametersPresentFlag);
		if(vclHrdParametersPresentFlag.get() == 1) {
			loadHrdParameters(loader);
		}
		if(nalHrdParametersPresentFlag.get() == 1 || vclHrdParametersPresentFlag.get() == 1) {
			lowDelayHrdFlag = new Bit1();
			loader.load(lowDelayHrdFlag);
		}
		picStructPresentFlag = new Bit1();
		bitstreamRestrictionFlag = new Bit1();
		loader.load(picStructPresentFlag, bitstreamRestrictionFlag);
		if(bitstreamRestrictionFlag.get() == 1) {
			motionVectorsOverPicBoundariesFlag = new Bit1();
			maxBytesPerPicDenom = new Ueg();
			maxBitsPerMbDenom = new Ueg();
			log2MaxMvLengthHorizontal = new Ueg();
			log2MaxMvLengthVertical = new Ueg();
			maxNumReorderFrames = new Ueg();
			maxDecFrameBuffering = new Ueg();
			loader.load(motionVectorsOverPicBoundariesFlag, maxBytesPerPicDenom, maxBitsPerMbDenom,
					log2MaxMvLengthHorizontal, log2MaxMvLengthVertical, maxNumReorderFrames, maxDecFrameBuffering);
		}
	}
	/**
	 * load hrdParameters
	 * can be loaded twice, however, both can have the same data.
	 * if different, throw the exception.
	 * @param loader
	 */
	private void loadHrdParameters(BitLoader loader) throws Exception {
		Ueg val1 = new Ueg();
		Bit4 val2 = new Bit4();
		Bit4 val3 = new Bit4();
//		cpbCntMinus1 = new Ueg();
//		bitRateScale = new Bit4();
//		cpbSizeScale = new Bit4();
		loader.load(val1, val2, val3);
		if(cpbCntMinus1 == null) {
			cpbCntMinus1 = val1;
		}
		else if(cpbCntMinus1.get() != val1.get()) {
			throw new RuntimeException("cpbCntMinus1 is different from previous one");
		}
		if(bitRateScale == null) {
			bitRateScale = val2;
		}
		else if(bitRateScale.get() != val2.get()) {
			throw new RuntimeException("bitRateScale is different from previous one");
		}
		if(cpbSizeScale == null) {
			cpbSizeScale = val3;
		}
		else if(cpbSizeScale.get() != val3.get()) {
			throw new RuntimeException("cpbSizeScale is different from previous one");
		}

		int num = cpbCntMinus1.get() + 1;
		if(bitRateValueMinus1 == null) {
			bitRateValueMinus1 = new Ueg[num];
		}
		if(cpbSizeValueMinus1 == null) {
			cpbSizeValueMinus1 = new Ueg[num];
		}
		if(cbrFlag == null) {
			cbrFlag = new Bit1[num];
		}
		for(int schedSelIdx = 0;schedSelIdx < num;schedSelIdx ++) {
			Ueg val4 = new Ueg();
			Ueg val5 = new Ueg();
			Bit1 val6 = new Bit1();
			loader.load(val4, val5, val6);
			if(bitRateValueMinus1[schedSelIdx] == null) {
				bitRateValueMinus1[schedSelIdx] = val4;
			}
			else if(bitRateValueMinus1[schedSelIdx].get() != val4.get()) {
				throw new RuntimeException("bitRateValueMinus1 is different from previous one");
			}
			if(cpbSizeValueMinus1[schedSelIdx] == null) {
				cpbSizeValueMinus1[schedSelIdx] = val5;
			}
			else if(cpbSizeValueMinus1[schedSelIdx].get() != val5.get()) {
				throw new RuntimeException("cpbSizeValueMinus1 is different from previous one");
			}
			if(cbrFlag[schedSelIdx] == null) {
				cbrFlag[schedSelIdx] = val6;
			}
			else if(cbrFlag[schedSelIdx].get() != val6.get()) {
				throw new RuntimeException("cbrFlag is different from previous one");
			}
			bitRateValueMinus1[schedSelIdx] = val4;
			cpbSizeValueMinus1[schedSelIdx] = val5;
			cbrFlag[schedSelIdx] = val6;
		}
		Bit5 val7 = new Bit5();
		Bit5 val8 = new Bit5();
		Bit5 val9 = new Bit5();
		Bit5 val10 = new Bit5();
		loader.load(val7, val8, val9, val10);
		if(initialCpbRemovalDelayLengthMinus1 == null) {
			initialCpbRemovalDelayLengthMinus1 = val7;
		}
		else if(initialCpbRemovalDelayLengthMinus1.get() != val7.get()) {
			throw new RuntimeException("initialCpbRemovalDelayLengthMinus1 is different from previous one");
		}
		if(cpbRemovalDelayLengthMinus1 == null) {
			cpbRemovalDelayLengthMinus1 = val8;
		}
		else if(cpbRemovalDelayLengthMinus1.get() != val8.get()) {
			throw new RuntimeException("cpbRemovalDelayLengthMinus1 is different from previous one");
		}
		if(dpbOutputDelayLengthMinus1 == null) {
			dpbOutputDelayLengthMinus1 = val9;
		}
		else if(dpbOutputDelayLengthMinus1.get() != val9.get()) {
			throw new RuntimeException("dpbOutputDelayLengthMinus1 is different from previous one");
		}
		if(timeOffsetLength == null) {
			timeOffsetLength = val10;
		}
		else if(timeOffsetLength.get() != val10.get()) {
			throw new RuntimeException("timeOffsetLength is different from previous one");
		}
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(IReadChannel channel) throws Exception {
//		channel.position(super.getReadPosition());
//		buffer = BufferUtil.safeRead(channel, getSize() - getReadPosition());
		super.update();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
		if(buffer == null) {
			throw new Exception("body data is undefined.");
		}
		// TODO この部分のこの方法は修正しないとだめ、自力でコネクトしてやるのではなく、大元のデータを保持しておいて、それを応答するようにする。
		// でないと、00 00 03の扱いを自力で作る必要がでてくるので、非常にややこしいことになる。
		// ここの結合動作をつくっておく必要がありそう。(でも読み込んだデータをベースにしておいた方がいいかも・・・)
/*		BitConnector connector = new BitConnector();
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
				buffer));*/
		setData(BufferUtil.connect(getTypeBuffer(), buffer));
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ByteBuffer getPackBuffer() {
		return null;
	}
	/**
	 * 
	 * @return
	 */
	public boolean getNalHrdBpPresentFlag() {
		if(nalHrdParametersPresentFlag != null && nalHrdParametersPresentFlag.get() == 1) {
			return true;
		}
		return false;
	}
	/**
	 * 
	 * @return
	 */
	public boolean getVclHrdBpPresentFlag() {
		if(vclHrdParametersPresentFlag != null && vclHrdParametersPresentFlag.get() == 1) {
			return true;
		}
		return false;
	}
	/**
	 * 
	 * @return
	 */
	public boolean getCpbDpbDelaysPresentFlag() {
		if(getNalHrdBpPresentFlag()) {
			return true;
		}
		if(getVclHrdBpPresentFlag()) {
			return true;
		}
		return false;
	}
	/**
	 * cpbCntMinus1の値を参照する
	 * @return
	 */
	public int getCpbCntMinus1() {
		if(cpbCntMinus1 == null) {
			throw new RuntimeException("cpbCntMinus1 is undefined, however, try to ref.");
		}
		return cpbCntMinus1.get();
	}
	public int getInitialCpbRemovalDelayLengthMinus1() {
		if(initialCpbRemovalDelayLengthMinus1 == null) {
			throw new RuntimeException("initialCpbRemovalDelayLengthMinus1 is undefined, however, try to ref.");
		}
		return initialCpbRemovalDelayLengthMinus1.get();
	}
	public int getPicStructPresentFlag() {
		if(picStructPresentFlag == null) {
			throw new RuntimeException("picStructPresentFlag is undefined, however, try to ref.");
		}
		return picStructPresentFlag.get();
	}
	public int getTimeOffsetLength() {
		if(timeOffsetLength == null) {
			throw new RuntimeException("timeoffsetLength is undefined, however, try to ref.");
		}
		return timeOffsetLength.get();
	}
}
