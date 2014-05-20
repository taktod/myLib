package com.ttProject.frame.vp9;

import org.apache.log4j.Logger;

import com.ttProject.frame.VideoSelector;
import com.ttProject.frame.vp9.type.KeyFrame;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.IUnit;

public class Vp9FrameSelector extends VideoSelector {
	/** ロガー */
	private Logger logger = Logger.getLogger(Vp9FrameSelector.class);
	/** 参照用のkeyFrameデータ */
	private KeyFrame keyFrame = null;
	@Override
	public IUnit select(IReadChannel channel) throws Exception {
		logger.info("frameを解析します。");
		return null;
	}
}
