package com.ttProject.frame.flv1;

import com.ttProject.frame.flv1.type.DisposableInterFrame;
import com.ttProject.frame.flv1.type.InterFrame;
import com.ttProject.frame.flv1.type.IntraFrame;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.ISelector;
import com.ttProject.unit.IUnit;
import com.ttProject.unit.extra.Bit;
import com.ttProject.unit.extra.Bit1;
import com.ttProject.unit.extra.Bit2;
import com.ttProject.unit.extra.Bit3;
import com.ttProject.unit.extra.Bit5;
import com.ttProject.unit.extra.Bit8;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.BitN.Bit16;
import com.ttProject.unit.extra.BitN.Bit17;

/**
 * flv1のframeを選択する動作
 * @author taktod
 */
public class Flv1FrameSelector implements ISelector {
	/**
	 * flv1のframeを決定します。
	 * @param channel (1frame分のデータが渡されることを期待します)
	 * @return
	 */
	public IUnit select(IReadChannel channel) throws Exception {
		BitLoader bitLoader = new BitLoader(channel);
		Bit17 pictureStartCode = new Bit17();
		Bit5 version = new Bit5();
		Bit8 temporalReference = new Bit8();
		Bit3 pictureSize = new Bit3();
		bitLoader.load(pictureStartCode,
				version, temporalReference, pictureSize);
		if(pictureStartCode.get() != 1) {
			throw new Exception("開始タグが想定外です。");
		}
		int width = 0;
		int height = 0;
		Bit customWidth = null;
		Bit customHeight = null;
		switch(pictureSize.get()) {
		case 0: // custom1
			customWidth = new Bit8();
			customHeight = new Bit8();
			bitLoader.load(customWidth, customHeight);
			width = customWidth.get();
			height = customHeight.get();
			break;
		case 1: // custom2
			customWidth = new Bit16();
			customHeight = new Bit16();
			bitLoader.load(customWidth, customHeight);
			width = customWidth.get();
			height = customHeight.get();
			break;
		case 2: // CIF
			width = 352;
			height = 288;
			break;
		case 3: // QCIF
			width = 176;
			height = 144;
			break;
		case 4: // SQCIF
			width =  128;
			height = 96;
			break;
		case 5: // 320x240
			width = 320;
			height = 240;
			break;
		case 6: // 160x120
			width = 160;
			height = 120;
			break;
		case 7: // reserved
			throw new Exception("pictureSizeがreservedになっていました。");
		}
		Bit2 pictureType = new Bit2();
		Bit1 deblockingFlag = new Bit1();
		Bit5 quantizer = new Bit5();
		Bit1 extraInformationFlag = new Bit1();
		bitLoader.load(pictureType, deblockingFlag, quantizer, extraInformationFlag);
		Bit8 extraInformation = null;
		if(extraInformationFlag.get() == 1) {
			extraInformation = new Bit8();
			bitLoader.load(extraInformation);
		}
		Flv1Frame frame = null;
		switch(pictureType.get()) {
		case 0: // intraFrame
			frame = new IntraFrame(pictureStartCode, version, temporalReference,
					pictureSize, customWidth, customHeight, width, height,
					pictureType, deblockingFlag, quantizer, extraInformationFlag,
					extraInformation, bitLoader.getExtraBit());
			break;
		case 1: // interFrame
			frame = new InterFrame(pictureStartCode, version, temporalReference,
					pictureSize, customWidth, customHeight, width, height,
					pictureType, deblockingFlag, quantizer, extraInformationFlag,
					extraInformation, bitLoader.getExtraBit());
			break;
		case 2: // disposableInterFrame
			frame = new DisposableInterFrame(pictureStartCode, version, temporalReference,
					pictureSize, customWidth, customHeight, width, height,
					pictureType, deblockingFlag, quantizer, extraInformationFlag,
					extraInformation, bitLoader.getExtraBit());
			break;
		case 3: // reserved
		default:
			throw new Exception("flv1想定外のフレームです。");
		}
		frame.setSize(channel.size());
		return frame;
	}
}
