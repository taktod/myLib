package com.ttProject.frame.h264.test;

import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ttProject.frame.h264.ConfigData;
import com.ttProject.frame.h264.type.SequenceParameterSet;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.IUnit;
import com.ttProject.util.HexUtil;

public class ConfigReadTest {
	private Logger logger = Logger.getLogger(ConfigReadTest.class);
	@Test
	public void test() throws Exception {
		IReadChannel channel = new ByteReadChannel(HexUtil.makeBuffer("014D401EFFE10019674D401E924201405FF2E02200000300C800002ED51E2C5C9001000468EE32C8"));
		// channelのデータを読み込んでpspとppsが取得できれば御の字
		ConfigData cdata = new ConfigData();
		List<IUnit> units = cdata.getNals(channel);
		SequenceParameterSet sps = (SequenceParameterSet)units.get(0);
		logger.info(sps.getWidth());
		logger.info(sps.getHeight());
	}
}
