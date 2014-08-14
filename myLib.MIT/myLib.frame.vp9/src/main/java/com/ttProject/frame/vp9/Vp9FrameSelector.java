/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.vp9;

import org.apache.log4j.Logger;

import com.ttProject.frame.VideoSelector;
import com.ttProject.frame.vp9.type.IntraFrame;
import com.ttProject.frame.vp9.type.KeyFrame;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.IUnit;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit2;
import com.ttProject.unit.extra.bit.Bit3;

/**
 * vp9のフレームのSelector
 * @author taktod
 */
public class Vp9FrameSelector extends VideoSelector {
	/** ロガー */
	private Logger logger = Logger.getLogger(Vp9FrameSelector.class);
	/** 参照用のkeyFrameデータ */
	private KeyFrame keyFrame = null;
	@Override
	public IUnit select(IReadChannel channel) throws Exception {
		if(channel.position() == channel.size()) {
			return null;
		}
		logger.info("frameを解析します。");
		Bit2 frameMarker = new Bit2();
		Bit1 profile = new Bit1();
		Bit1 reservedBit = new Bit1(); // 0のはず
		Bit1 refFlag = new Bit1(); // referenceFrameであるかどうかか？ bitが立っている場合は次の3bitがref番号になっているはず？
		Bit3 ref = null; // とりあえずこれがこないことを祈る
		Bit1 keyFrameFlag = new Bit1(); // 反転
		Bit1 invisibleFlag = new Bit1(); // 反転
		Bit1 errorRes = new Bit1();
		// ここまで読み込めばどういうフレームかわかるはず。
		BitLoader loader = new BitLoader(channel);
		loader.load(frameMarker, profile, reservedBit, refFlag);
		if(refFlag.get() == 1) {
			ref = new Bit3();
			loader.load(ref);
			throw new Exception("refFlagの読み込みはどうなっているかわかりません");
		}
		loader.load(keyFrameFlag, invisibleFlag, errorRes);
		Vp9Frame frame = null;
		if(keyFrameFlag.get() == 0) {
			logger.info("kerFrame");
			frame = new KeyFrame(frameMarker, profile, reservedBit, refFlag, keyFrameFlag, invisibleFlag, errorRes);
			keyFrame = (KeyFrame)frame;
		}
		else {
			logger.info("intraFrame");
			frame = new IntraFrame(frameMarker, profile, reservedBit, refFlag, keyFrameFlag, invisibleFlag, errorRes);
		}
		if((frame instanceof KeyFrame)) {
			frame.setKeyFrame(keyFrame);
		}
		frame.minimumLoad(channel);
		return frame;
	}
}
