/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.nellymoser.type;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import com.ttProject.frame.nellymoser.NellymoserFrame;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.BitConnector;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.BitN;
import com.ttProject.unit.extra.bit.Bit32;
import com.ttProject.unit.extra.bit.Bit5;
import com.ttProject.unit.extra.bit.Bit6;

/**
 * nellymoser frame
 * @see http://wiki.multimedia.cx/index.php?title=Nelly_Moser
 * nellymoserのframeは1つあたり
 * header + payload + payloadの組み合わせになっています。
 * 最少単位は0x40で構成されるみたいです。
 * header部が6bit(initTableIndex) + 22個の5bit(deltaTable) = 116bit
 * payloadは198bit、これが２つとなります。
 * 116 + 198 + 198 = 512bit -> 64byte -> 0x40となります。
 * flvの場合はnellymoserはモノラルのみらしいです。
 * また、flvのaudioTagには、このデータが1,2,4個含む形ではいっているとのことです。
 * 上記のwikiより
 * 
 * よってsample数をみたいなら、0x40の塊の数 x 256で割り出せることになります。
 * nelly16 nelly8の場合はmonoral強制ですが、そのほかの場合はstereoも仕様上は作成可能っぽいです。
 * その場合0x40がベースになるか0x80がベースになるかは未調査です。
 * @author taktod
 */
public class Frame extends NellymoserFrame {
	/** logger */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(Frame.class);
	private Bit6   initTableIndex = null;
	private Bit5[] deltaTable = null; //new Bit5[22];
	private BitN   payload1 = null; //new BitN(new Bit32(), new Bit32(), new Bit32(), new Bit32(), new Bit32(), new Bit32(), new Bit6());
	private BitN   payload2 = null; //new BitN(new Bit32(), new Bit32(), new Bit32(), new Bit32(), new Bit32(), new Bit32(), new Bit6());
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		super.setReadPosition(channel.position());
		super.setSize(64);
		super.setSampleNum(256);
		super.update();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(IReadChannel channel) throws Exception {
		BitLoader loader = new BitLoader(channel);
		initTableIndex = new Bit6();
		deltaTable = new Bit5[22];
		loader.load(initTableIndex);
		for(int i = 0;i < 22;i ++) {
			deltaTable[i] = new Bit5();
		}
		loader.load(deltaTable);
		payload1 = new BitN(new Bit32(), new Bit32(), new Bit32(), new Bit32(), new Bit32(), new Bit32(), new Bit6());
		payload2 = new BitN(new Bit32(), new Bit32(), new Bit32(), new Bit32(), new Bit32(), new Bit32(), new Bit6());
		loader.load(payload1);
		loader.load(payload2);
		super.update();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
		if(initTableIndex == null) {
			throw new Exception("is not loaded.");
		}
		BitConnector connector = new BitConnector();
		connector.feed(initTableIndex);
		connector.feed(deltaTable);
		connector.feed(payload1);
		connector.feed(payload2);
		super.setData(connector.connect());
	}
	/**
	 * {@inheritDoc}
	 * NOTE this packBuffer can be multiple frame.
	 * 64byte 128byte 256byte
	 */
	@Override
	public ByteBuffer getPackBuffer() throws Exception {
		return getData();
	}
}
