package com.ttProject.frame.adpcmswf.type;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import com.ttProject.frame.adpcmswf.AdpcmswfFrame;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.Bit;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit16;
import com.ttProject.unit.extra.bit.Bit2;
import com.ttProject.unit.extra.bit.Bit6;
import com.ttProject.util.BufferUtil;

/**
 * adpcmswfのframeデータ
 * とりあえずsampleNumだけ取得したい。
 * adpcmCodeSizeを取得してから
 * (byte数 - 0埋めbits) / (adpcmCodeSize + 2) / (channel数) + 1でsampleNumを取得したい。
 * 最終byteを確認して0でうまっている部分はできるだけ排除
 * にすれば、より正確になりそう。
 * @author taktod
 */
public class Frame extends AdpcmswfFrame {
	/** ロガー */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(Frame.class);
	private Bit2 adpcmCodeSize = new Bit2();
	private Bit16 initSample1 = new Bit16();
	private Bit6 initialIndex1 = new Bit6();
	private Bit16 initSample2 = null;
	private Bit6 initialIndex2 = null;
	private Bit extraBit = null;
	private ByteBuffer buffer = null;
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		setSize(channel.size());
		BitLoader loader = new BitLoader(channel);
		loader.load(adpcmCodeSize,
				initSample1, initialIndex1);
		if(getChannel() == 2) {
			initSample2 = new Bit16();
			initialIndex2 = new Bit6();
			loader.load(initSample2, initialIndex2);
		}
		// 残りデータサイズ計算
		extraBit = loader.getExtraBit();
		// データサイズからsampleRateを割出します。
		setReadPosition(channel.position());
		// 残りbit数を計算しておく。
		double leftBitCount = (channel.size() - channel.position()) * 8 + (extraBit != null ? extraBit.getBitCount() : 0);
		if(getChannel() == 2) {
			// ステレオの場合
			setSampleNum((int)Math.ceil(leftBitCount / (adpcmCodeSize.get() + 2) / 2));
		}
		else {
			// モノラルの場合
			setSampleNum((int)Math.ceil(leftBitCount / (adpcmCodeSize.get() + 2)));
		}
	}
	@Override
	public void load(IReadChannel channel) throws Exception {
		channel.position(getReadPosition());
		buffer = BufferUtil.safeRead(channel, getSize() - getReadPosition());
	}
	@Override
	protected void requestUpdate() throws Exception {
	}
}
