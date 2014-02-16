package com.ttProject.media.h264.test;

import com.ttProject.media.h264.frame.SequenceParameterSet;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.util.HexUtil;

/**
 * spsのデータのロード動作テスト
 * @author taktod
 */
public class SpsLoadTest {
	/**
	 * 読み込み動作テストします。
	 * @throws Exception
	 */
//	@Test
	public void test() throws Exception {
		IReadChannel channel = new ByteReadChannel(HexUtil.makeBuffer("4D401E924201405FF2E02200000300C800002ED51E2C5C90")); // 640 x 360
//		IReadChannel channel = new ByteReadChannel(HexUtil.makeBuffer("640015acc86020096c04400000030040000007a3c58b6780")); // 512 x 288
		SequenceParameterSet sps = new SequenceParameterSet((byte)7);
		sps.setSize(0x19);
		sps.analyze(channel);
	}
}
