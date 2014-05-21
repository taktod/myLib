package com.ttProject.frame.vp9.type;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import com.ttProject.frame.vp9.Vp9Frame;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit16;
import com.ttProject.unit.extra.bit.Bit2;
import com.ttProject.unit.extra.bit.Bit3;
import com.ttProject.util.BufferUtil;
import com.ttProject.util.HexUtil;

public class KeyFrame extends Vp9Frame {
	/** ロガー */
	private Logger logger = Logger.getLogger(KeyFrame.class);
	private byte[] syncBit = {};
	private Bit3 colorSpace    = new Bit3();
	private Bit1 fullrange     = new Bit1();
	private Bit16 widthMinus1  = new Bit16();
	private Bit16 heightMinus1 = new Bit16();
	/**
	 * コンストラクタ
	 * @param frameMarker
	 * @param profile
	 * @param reserved
	 * @param refFlag
	 * @param keyFrameFlag
	 * @param invisibleFlag
	 * @param errorRes
	 */
	public KeyFrame(Bit2 frameMarker, Bit1 profile, Bit1 reserved, Bit1 refFlag,
			Bit1 keyFrameFlag, Bit1 invisibleFlag, Bit1 errorRes) {
		super(frameMarker, profile, reserved, refFlag, keyFrameFlag, invisibleFlag, errorRes);
	}
	@Override
	public ByteBuffer getPackBuffer() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		logger.info("minimumLoadを実施します。");
		// ここでframeのwidth x heightあたりはおさえておきたいところ。
		ByteBuffer syncBuffer = BufferUtil.safeRead(channel, 3);
		if((syncBuffer.get() & 0xFF) != 0x49
		|| (syncBuffer.get() & 0xFF) != 0x83
		|| (syncBuffer.get() & 0xFF) != 0x42) {
			throw new Exception("syncBufferがおかしいです。");
		}
		BitLoader loader = new BitLoader(channel);
		loader.load(colorSpace);
		if(colorSpace.get() == 7) { // rgb = profile 1
			throw new Exception("RGBはprofile0でサポートされていません。");
		}
		loader.load(fullrange, widthMinus1, heightMinus1);
		logger.info("width:" + (widthMinus1.get() + 1));
		logger.info("height:" + (heightMinus1.get() + 1));
	}

	@Override
	public void load(IReadChannel channel) throws Exception {
		// ここで残りフレームのデータの読み込み完了を実施しないとだめ。
		channel.position(channel.size());
	}

	@Override
	protected void requestUpdate() throws Exception {
		// TODO Auto-generated method stub
		
	}
}
