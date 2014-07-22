/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.opus.type;

import java.nio.ByteBuffer;
import java.util.List;

import org.apache.log4j.Logger;

import com.ttProject.frame.opus.OpusFrame;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.BitConnector;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit16;
import com.ttProject.unit.extra.bit.Bit32;
import com.ttProject.unit.extra.bit.Bit8;
import com.ttProject.util.BufferUtil;
import com.ttProject.util.HexUtil;

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
 * @see http://tools.ietf.org/pdf/draft-ietf-codec-oggopus-03.pdf
 * 
 * とりあえずmappingなしでも成立するっぽいので、このまま攻めて見ようと思う。
 */
public class HeaderFrame extends OpusFrame {
	/** ロガー */
	private Logger logger = Logger.getLogger(HeaderFrame.class);
	private String opusString = "OpusHead";
	private Bit8  version              = new Bit8();  // 1固定らしい
	private Bit8  channels             = new Bit8();
	private Bit16 preSkip              = new Bit16(); // デコードするときの遅延フレーム数か？サンプルだったら312になった
	private Bit32 sampleRate           = new Bit32(); // どうやら入力サンプルレートで出力時のではないっぽい。(どういうことだ？)出力時のサンプルレートはデコードの仕方でいろいろにできるっぽい。(8,12,16,24,48kHzが動作可能なものっぽい)
	private Bit16 outputGain           = new Bit16(); // 出力振幅の設定か？(10^(outputGain / 20.0*256)になるのか？)
	private Bit8  channelMappingFamily = new Bit8();

	// 以下のデータはoptionalChannelMappingTableからくるけど、channelMappingFamilyが0でもデータをいれておいた方がよさそう(デフォルトってやつ)
	private int nbStreams;
	private int nbCoupled;
	private List<Integer> streamMap;
	// Optional Channel Mapping Table
/*
	channelMappingFamilyが0でない場合の動作
	Bit8 nbStreamの数
	Bit8 nbCoupled nbStreamより小さな値であるべき、nbCoupled + nbStreamsを足して255を超えたらストリームおおすぎ
	

	channels > 2の場合はエラー
	streams = 1
	nbCoupled = チャンネル数 > 1ならかどうかで判断
	stream_map[0] = 0
	stream_map[1] = 1
	*/
	// mappingTableもあるかもしれないがほっとく。
	public HeaderFrame() {
		super.update();
	}
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		super.setReadPosition(channel.position());
		super.setSize(channel.size());
		BitLoader loader = new BitLoader(channel);
		loader.setLittleEndianFlg(true);
		loader.load(version, channels, preSkip, sampleRate,
				outputGain, channelMappingFamily);
		logger.info(channels.get());
		logger.info(sampleRate.get());
		if(channel.position() != channel.size()) {
			throw new Exception("mappingTableがあるデータでした、解析する必要があるので、開発者に問い合わせてください。");
		}
		super.update();
	}
	@Override
	public void load(IReadChannel channel) throws Exception {
		// 特にやることなし
	}
	@Override
	protected void requestUpdate() throws Exception {
		// 全データの結合をつくる必要あり。
		BitConnector connector = new BitConnector();
		connector.setLittleEndianFlg(true);
		ByteBuffer buffer = ByteBuffer.wrap(opusString.getBytes());
		super.setData(BufferUtil.connect(
				buffer,
				connector.connect(version, channels, preSkip, sampleRate, outputGain, channelMappingFamily)));
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

