/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.media.h264;

import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.util.BufferUtil;

/**
 * flvやmp4にはいっているnalの中身と同じデータは00 00 01に続くデータではなく、サイズ(4バイト)に続く形ではいっている模様です。
 * @author taktod
 *
 */
public class DataNalAnalyzer extends FrameAnalyzer {
	@Override
	public Frame analyze(IReadChannel ch) throws Exception {
		if(ch.size() < 4) {
			throw new Exception("読み込みバッファ量がおかしいです。");
		}
		int size = BufferUtil.safeRead(ch, 4).getInt();
		// 以降、このサイズがデータ
		// 始めの1バイトを読み込んでどういうデータか確認する。
		if(size <= 0) {
			throw new Exception("データ指定がおかしいです。");
		}
		if(ch.size() - ch.position() < size) {
			throw new Exception("データが足りません。");
		}
		// byteをみてデータがなにであるか確認する必要がありそう。
		Frame frame = super.analyze(ch);
		frame.setSize(size);
		frame.analyze(ch);
		return frame;
	}
}
