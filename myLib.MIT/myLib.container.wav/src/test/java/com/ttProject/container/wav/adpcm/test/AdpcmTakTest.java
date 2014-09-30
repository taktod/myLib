/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.wav.adpcm.test;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ttProject.container.IContainer;
import com.ttProject.container.IReader;
import com.ttProject.container.riff.IFrameEventListener;
import com.ttProject.container.riff.RiffUnitReader;
import com.ttProject.container.riff.type.Data;
import com.ttProject.frame.AudioFrame;
import com.ttProject.frame.IFrame;
import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IFileReadChannel;

/**
 * adpcmの僕オリジナル圧縮形式テスト
 * @author taktod
 */
public class AdpcmTakTest {
	/** ロガー */
	private Logger logger = Logger.getLogger(AdpcmTakTest.class);
	private 			Integer[] order = {
			0x0,
			0x8,
			0x9,
			0x1,
			0x2,
			0xA,
			0xB,
			0x3,
			0x4,
			0x5,
			0xC,
			0xD,
			0x6,
			0x7,
			0xE,
			0xF
	};
	private Integer[][] table = {
			{0x0, 6},
			{0x8, 6},
			{0x9, 3},
			{0x1, 3},
			{0x2, 3},
			{0xA, 3},
			{0xB, 3},
			{0x3, 2},
			{0x4, 2},
			{0x5, 2},
			{0xC, 2},
			{0xD, 2},
			{0x6, 1},
			{0x7, 1},
			{0xE, 1},
			{0xF, 1}
	};

	/**
	 * 読み込みテスト
	 */
	@Test
	public void test() {
		IFileReadChannel source = null;
		try {
			source = FileReadChannel.openFileReadChannel(
					Thread.currentThread().getContextClassLoader().getResource("test.adpcm_ima_wav.wav")
			);
			IReader reader = new RiffUnitReader();
			IContainer container = null;
			RangeCoder coder = new RangeCoder();
			coder.setupTable(table);
			while((container = reader.read(source)) != null) {
				logger.info(container);
				if(container instanceof Data) {
					Data data = (Data) container;
					data.analyzeFrame(source, new IFrameEventListener() {
						@Override
						public void onNewFrame(IFrame frame) {
							if(frame instanceof AudioFrame) {
								AudioFrame aFrame = (AudioFrame)frame;
								// フレームが取得できたので圧縮かけてみる。
								try {
									ByteBuffer orgBuffer = aFrame.getData();
									// まず上位２つの一番良く出てくる符号を調べる
									ByteBuffer targetBuffer = orgBuffer.duplicate();
									int[] rank = new int[16];
									while(targetBuffer.remaining() > 0) {
										byte b = targetBuffer.get();
										rank[(b >>> 4) & 0x0F] ++;
										rank[b & 0xF] ++;
									}
									int first = 0;
									int second = 0;
									for(int i = 0;i < rank.length;i ++) {
										if(rank[first] < rank[i]) {
											first = i;
										}
//										logger.info(i + ":" + rank[i]);
									}
									for(int i = 0;i < rank.length;i ++) {
										if(i == first) {
											continue;
										}
										if(rank[second] < rank[i]) {
											second = i;
										}
									}
									// このタイミングでcoderのtableを更新する必要がある。
									table[0][0] = first;
									table[1][0] = second;
									int j = 2;
									for(int i = 0;i < 16;i ++) {
										if(order[i] == first || order[i] == second) {
											continue;
										}
										table[j ++][0] = order[i];
									}
									RangeCoder coder = new RangeCoder();
									coder.setupTable(table);
									// データをいれてセットアップする。
									targetBuffer.position(0); // もとに戻す
									while(targetBuffer.remaining() > 0) {
										byte b = targetBuffer.get();
										coder.encodeData((b >>> 4) & 0x0F);
										coder.encodeData(b & 0x0F);
									}
									ByteBuffer result = coder.getEncodeResult();
									logger.info("org:" + orgBuffer.remaining() +
											" codered:" + result.remaining() +
											" rate:" + (int)(result.remaining() * 100 / orgBuffer.remaining()));
								}
								catch(Exception e) {
									e.printStackTrace();
								}
							}
						}
					});
				}
			}
		}
		catch(Exception e) {
			logger.warn(e);
		}
		finally {
			if(source != null) {
				try {
					source.close();
				}
				catch(Exception e) {
				}
				source = null;
			}
		}
	}
}
