package com.ttProject.frame.adpcmswf.type;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import com.ttProject.frame.adpcmswf.AdpcmswfFrame;
import com.ttProject.nio.channels.IReadChannel;
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
	private ByteBuffer buffer = null;
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		setSize(channel.size());
		// データサイズからsampleRateを割出します。
		setReadPosition(channel.position());
		if(getChannel() == 2) {
			// ステレオの場合
			setSampleNum(getSize() - 1 - 4);
		}
		else {
			// モノラルの場合
			setSampleNum(getSize() - 1 - 2);
		}
	}
	@Override
	public void load(IReadChannel channel) throws Exception {
		channel.position(getReadPosition());
		buffer = BufferUtil.safeRead(channel, getSize());
	}
	@Override
	protected void requestUpdate() throws Exception {
	}
}
