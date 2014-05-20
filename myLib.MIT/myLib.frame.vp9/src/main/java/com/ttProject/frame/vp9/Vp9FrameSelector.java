package com.ttProject.frame.vp9;

import org.apache.log4j.Logger;

import com.ttProject.frame.VideoSelector;
import com.ttProject.frame.vp9.type.KeyFrame;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.IUnit;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit2;
import com.ttProject.unit.extra.bit.Bit3;
import com.ttProject.util.BufferUtil;
import com.ttProject.util.HexUtil;

public class Vp9FrameSelector extends VideoSelector {
	/** ロガー */
	private Logger logger = Logger.getLogger(Vp9FrameSelector.class);
	/** 参照用のkeyFrameデータ */
	private KeyFrame keyFrame = null;
	@Override
	public IUnit select(IReadChannel channel) throws Exception {
		logger.info("frameを解析します。");
		logger.info(HexUtil.toHex(BufferUtil.safeRead(channel, channel.size()), true));
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
			throw new Exception("refFlagの読み込みはどうなっているかわかりません");
		}
		loader.load(keyFrameFlag, invisibleFlag, errorRes);
		if(keyFrameFlag.get() == 0) {
			logger.info("kerFrame");
		}
		else {
			logger.info("intraFrame");
		}
		return null;
	}
}
