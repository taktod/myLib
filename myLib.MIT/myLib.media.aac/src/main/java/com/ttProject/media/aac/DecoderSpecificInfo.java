/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.media.aac;

import java.nio.ByteBuffer;

import com.ttProject.media.aac.frame.Aac;
import com.ttProject.media.extra.Bit1;
import com.ttProject.media.extra.Bit4;
import com.ttProject.media.extra.Bit5;
import com.ttProject.media.extra.BitConnector;
import com.ttProject.media.extra.BitLoader;
import com.ttProject.nio.channels.IReadChannel;

/**
 * aacのdecode specific infoのデータから
 * @see http://wiki.multimedia.cx/index.php?title=MPEG-4_Audio
 * @author taktod
 */
public class DecoderSpecificInfo {
	private Bit5 objectType1; // profileの事
//		private Bit6 objectType2;
	private Bit4 frequencyIndex; // samplingFrequenceIndexと同じ
//		private int frequency; // 24bit indexが0x0Fの場合
	private Bit4 channelConfiguration;
	private Bit1 frameLengthFlag; // 0:each packetcontains 1024 samples 1:960 samples
	private Bit1 dependsOnCoreCoder;
	private Bit1 extensionFlag;
	public DecoderSpecificInfo() {
		objectType1 = new Bit5(1); // デフォルトmainにしておく、一応・・・
		frequencyIndex = new Bit4();
		channelConfiguration = new Bit4();
		frameLengthFlag = new Bit1();
		dependsOnCoreCoder = new Bit1();
		extensionFlag = new Bit1();
	}
	public void analyze(IReadChannel channel) throws Exception {
		BitLoader bitLoader = new BitLoader(channel);
		bitLoader.load(objectType1, frequencyIndex, channelConfiguration,
				frameLengthFlag, dependsOnCoreCoder, extensionFlag);
		if(objectType1.get() == 0x1F) {
			// objectType2のデータ分 + 32が目標のプロファイルとなります。
			// サンプルデータがないのでサンプルみつけたら実装する予定。
			throw new Exception("objectTypeが別途処理になっていて処理できません。");
		}
		if(frequencyIndex.get() == 0x0F) {
			// 24bitがそのままfrequencyになる。
			// こちらもサンプルデータがないので、とりあえず見送り
			throw new Exception("frequencyが別途読み込みになっていて処理できません。");
		}
	}
	public void analyze(Aac frame) {
		objectType1 = new Bit5(frame.getProfile());
		frequencyIndex = new Bit4(frame.getSamplingFrequenceIndex());
		channelConfiguration = new Bit4(frame.getChannelConfiguration());
	}
	public ByteBuffer getInfoBuffer() throws Exception {
		BitConnector bitConnector = new BitConnector();
		return bitConnector.connect(objectType1, frequencyIndex, channelConfiguration,
				frameLengthFlag, dependsOnCoreCoder, extensionFlag);
	}
	public int getObjectType() {
		return objectType1.get();
	}
	public int getFrequenctIndex() {
		return frequencyIndex.get();
	}
	public int getChannelConfiguration() {
		return channelConfiguration.get();
	}
	@Override
	public String toString() {
		StringBuilder data = new StringBuilder("decoderSpecificInfo:");
		data.append(" ot:").append(objectType1);
		data.append(" fi:").append(frequencyIndex);
		data.append(" cc:").append(channelConfiguration);
		data.append(" flf:").append(frameLengthFlag);
		data.append(" docc").append(dependsOnCoreCoder);
		data.append(" ef:").append(extensionFlag);
		return data.toString();
	}
}
