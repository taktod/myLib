package com.ttProject.media.h264.test;

import org.junit.Test;

import com.ttProject.media.h264.ConfigData;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.util.HexUtil;

public class ConfigDataTest {
	@Test
	public void test() throws Exception {
		IReadChannel channel = new ByteReadChannel(HexUtil.makeBuffer("014D401EFFE10019674D401E924201405FF2E02200000300C800002ED51E2C5C9001000468EE32C8"));
		// channelのデータを読み込んでpspとppsが取得できれば御の字
		ConfigData cdata = new ConfigData();
		cdata.getNals(channel);
	}
}
