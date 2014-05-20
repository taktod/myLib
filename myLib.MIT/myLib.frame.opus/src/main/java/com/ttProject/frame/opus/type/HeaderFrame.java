package com.ttProject.frame.opus.type;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import com.ttProject.frame.opus.OpusFrame;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit16;
import com.ttProject.unit.extra.bit.Bit32;
import com.ttProject.unit.extra.bit.Bit8;
import com.ttProject.util.BufferUtil;

/**
 * opusのheaderFrame
 * matroskaだと、codecPrivateの中にはいっているデータ
 * 8byte opusString OpusHead
 * 1byte version
 * 1byte channels
 * 2byte preskip
 * 4byte sampleRate
 * 2byte outputGain
 * 1byte channelMappingFamily
 * nbyte mappingTable
 * @author taktod
 */
public class HeaderFrame extends OpusFrame {
	/** ロガー */
	private Logger logger = Logger.getLogger(HeaderFrame.class);
	private String opusString;
	private Bit8  version              = new Bit8();
	private Bit8  channels             = new Bit8();
	private Bit16 preSkip              = new Bit16();
	private Bit32 sampleRate           = new Bit32();
	private Bit16 outputGain           = new Bit16();
	private Bit8  channelMappingFamily = new Bit8();
	// mappingTableもあるかもしれないがほっとく。
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		opusString = new String(BufferUtil.safeRead(channel, 8).array());
		logger.info(opusString);
		BitLoader loader = new BitLoader(channel);
		loader.setLittleEndianFlg(true);
		loader.load(version, channels, preSkip, sampleRate,
				outputGain, channelMappingFamily);
		logger.info(channels.get());
		logger.info(sampleRate.get());
	}
	@Override
	public void load(IReadChannel channel) throws Exception {
		// 特にやることなし
	}
	@Override
	protected void requestUpdate() throws Exception {
		// 全データの結合をつくる必要あり。
	}
	@Override
	public ByteBuffer getPackBuffer() throws Exception {
		return null;
	}
	@Override
	public boolean isComplete() {
		return true;
	}
}

