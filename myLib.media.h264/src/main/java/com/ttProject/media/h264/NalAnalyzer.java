package com.ttProject.media.h264;

import com.ttProject.nio.channels.IReadChannel;

/**
 * nalデータの解析を実行します。
 * ただし、nalデータの印の部分00 00 00 01 or 00 00 01の部分には興味がないので、
 * データとしては、その部分を省いたデータを応答することにします。
 * 
 * なおこれはまだ作成する必要はなさそう。(mpegtsの読み込みくらいしか使いどころなさそうだから、
 * 他のデータmp4やflvはnalではない方法をつかっているみたいです。)
 * @author taktod
 *
 */
public class NalAnalyzer extends FrameAnalyzer {
	@Override
	public Frame analyze(IReadChannel ch) throws Exception {
		return null;
	}
}
