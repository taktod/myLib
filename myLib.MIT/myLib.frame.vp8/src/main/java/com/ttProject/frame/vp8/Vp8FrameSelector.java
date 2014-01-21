package com.ttProject.frame.vp8;

import org.apache.log4j.Logger;

import com.ttProject.frame.VideoSelector;
import com.ttProject.frame.vp8.type.IntraFrame;
import com.ttProject.frame.vp8.type.KeyFrame;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.IUnit;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.BitN;
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit19;
import com.ttProject.unit.extra.bit.Bit3;
import com.ttProject.unit.extra.bit.Bit8;

public class Vp8FrameSelector extends VideoSelector {
	/** ロガー */
	private Logger logger = Logger.getLogger(Vp8FrameSelector.class);
	/** 参照用のkeyFrameデータ */
	private KeyFrame keyFrame = null;
	/**
	 * {@inheritDoc}
	 */
	@Override
	public IUnit select(IReadChannel channel) throws Exception {
		logger.info("frameを解析します。");
		// 先頭の3byteからframeType version showFrame, firstPartSizeを取り出す
		BitLoader loader = new BitLoader(channel);
		Bit1 frameType = new Bit1();
		Bit3 version = new Bit3();
		Bit1 showFrame = new Bit1();
		Bit19 firstPartSize = new Bit19();
		Bit3 size_1 = new Bit3();
		Bit8 size_2 = new Bit8();
		Bit8 size_3 = new Bit8();
		loader.load(size_1, showFrame, version, frameType, size_2, size_3);
		BitN bit = new BitN(size_3, size_2, size_1);
		firstPartSize.set(bit.get());
		Vp8Frame frame = null;
		switch(frameType.get()) {
		case 0: // keyFrame
			frame = new KeyFrame(frameType, version, showFrame, firstPartSize);
			keyFrame = (KeyFrame)frame;
			break;
		case 1: // intraFrame
			frame = new IntraFrame(frameType, version, showFrame, firstPartSize);
			frame.setKeyFrame(keyFrame);
			break;
		default:
			throw new Exception("解析不能なデータです");
		}
		frame.minimumLoad(channel);
		return frame;
	}

}
