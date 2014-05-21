package com.ttProject.frame.h265.type;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import com.ttProject.frame.h265.H265Frame;
import com.ttProject.frame.h265.ProfileTierLevel;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit3;
import com.ttProject.unit.extra.bit.Bit4;
import com.ttProject.unit.extra.bit.Bit6;
import com.ttProject.unit.extra.bit.Ueg;

public class SpsNut extends H265Frame {
	/** ロガー */
	private Logger logger = Logger.getLogger(SpsNut.class);
	private Bit4 spsVideoParameterSetId = new Bit4();
	private Bit3 spsMaxSubLayersMinus1 = new Bit3();
	private Bit1 spsTemporalIdNestingFlag = new Bit1();
	private ProfileTierLevel profileTierLevel = new ProfileTierLevel();
	private Ueg spsSeqParameterSetId = new Ueg();
	private Ueg chromaFormatIdc = new Ueg();
	private Bit1 separateColourPlaneFlag = null;
	private Ueg picWidthInLumaSamples = new Ueg();
	private Ueg picHeightInLumaSamples = new Ueg();
	private Bit1 conformanceWindowFlag = new Bit1();
	/** データ */
	private ByteBuffer buffer = null;
	/**
	 * コンストラクタ
	 */
	public SpsNut(Bit1 forbiddenZeroBit,
			Bit6 nalUnitType,
			Bit6 nuhLayerId,
			Bit3 nuhTemporalIdPlus1) {
		super(forbiddenZeroBit, nalUnitType, nuhLayerId, nuhTemporalIdPlus1);
	}
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		// TODO Auto-generated method stub
		BitLoader loader = new BitLoader(channel);
		loader.setEmulationPreventionFlg(true);
		loader.load(spsVideoParameterSetId, spsMaxSubLayersMinus1,
				spsTemporalIdNestingFlag);
		// 96 / 8 = 12;
		profileTierLevel.minimumLoad(loader, spsMaxSubLayersMinus1.get());
		// ここでspsの続きを読み込みます。
		loader.load(spsSeqParameterSetId, chromaFormatIdc);
		if(chromaFormatIdc.get() == 3) {
			// separateColurPlaneFlagを読み込む
			separateColourPlaneFlag = new Bit1();
			loader.load(separateColourPlaneFlag);
		}
		loader.load(picWidthInLumaSamples, picHeightInLumaSamples,
				conformanceWindowFlag);
		logger.info("width:" + picWidthInLumaSamples.get());
		logger.info("height:" + picHeightInLumaSamples.get());
		// 後のデータは適当にやっとく。
	}
	@Override
	public void load(IReadChannel channel) throws Exception {
		// TODO Auto-generated method stub
	}
	@Override
	protected void requestUpdate() throws Exception {
		// TODO Auto-generated method stub
	}
	@Override
	public ByteBuffer getPackBuffer() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
