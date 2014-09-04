/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.mkv.test;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ttProject.container.IContainer;
import com.ttProject.container.IReader;
import com.ttProject.container.IWriter;
import com.ttProject.container.mkv.MkvBlockTag;
import com.ttProject.container.mkv.MkvTagReader;
import com.ttProject.container.mkv.MkvTagWriter;
import com.ttProject.container.mkv.type.TrackEntry;
import com.ttProject.frame.CodecType;
import com.ttProject.frame.IFrame;
import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IReadChannel;

/**
 * mkvによるデータの書き込み動作テスト
 * @author taktod
 */
public class MkvWriteTest {
	/** ロガー */
	private Logger logger = Logger.getLogger(MkvWriteTest.class);
	/**
	 * 動作テスト
	 * @throws Exception
	 */
	@Test
	public void test() throws Exception {
		logger.info("書き込みテスト開始");
		IReadChannel source = FileReadChannel.openFileReadChannel(
				Thread.currentThread().getContextClassLoader().getResource("test.h264aac.mkv")
		);
		IReader reader = new MkvTagReader();
		IWriter writer = new MkvTagWriter("output.mkv");
		writer.prepareHeader(CodecType.H264, CodecType.AAC);
		// mpegtsとかでもあらかじめ書き込みを実行することで対処しているので、mkvもそうすればいいかな・・・
		// ある程度どうなるかわかっているとだいぶ助かるけど・・・
		// frameが着てから解析するというのはやらないでおこうというのがいいのか？
		// ただしmpegtsと違ってサイズやサンプルレートといった情報も必要になるので、そのあたりもきちんと調整しておいた方がよさそうですね。
		IContainer container = null;
		while((container = reader.read(source)) != null) {
			if(container instanceof TrackEntry) {
				logger.info(container);
			}
			if(container instanceof MkvBlockTag) {
				MkvBlockTag blockTag = (MkvBlockTag) container;
				IFrame frame = blockTag.getFrame();
//				logger.info(frame);
				writer.addFrame(blockTag.getTrackId().get(), frame);
			}
		}
		writer.prepareTailer();
		logger.info("処理おわり");
	}
}
