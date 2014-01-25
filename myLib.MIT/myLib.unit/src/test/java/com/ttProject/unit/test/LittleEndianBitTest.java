package com.ttProject.unit.test;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit16;
import com.ttProject.unit.extra.bit.Bit24;
import com.ttProject.unit.extra.bit.Bit3;
import com.ttProject.unit.extra.bit.Bit4;
import com.ttProject.unit.extra.bit.Bit8;

/**
 * littleEndianの場合のbit操作動作テスト
 * @author taktod
 */
public class LittleEndianBitTest {
	/** ロガー */
	private Logger logger = Logger.getLogger(LittleEndianBitTest.class);
	@Test
	public void test1() throws Exception {
		IReadChannel channel = new ByteReadChannel(new byte[]{
				0x29
		});
		Bit1 a = new Bit1();
		Bit3 b = new Bit3();
		Bit4 c = new Bit4();
		BitLoader loader = new BitLoader(channel);
		loader.setLittleEndianFlg(true);
		loader.load(a, b, c);
		logger.info(a);
		logger.info(b);
		logger.info(c);
	}
	@Test
	public void test2() throws Exception {
		IReadChannel channel = new ByteReadChannel(new byte[] {
				0x01, 0x02, 0x03, 0x04, 0x05, 0x06
		});
		Bit8 a  = new Bit8();
		Bit16 b = new Bit16();
		Bit24 c = new Bit24();
		BitLoader loader = new BitLoader(channel);
		loader.setLittleEndianFlg(true);
		loader.load(a, b, c);
		logger.info(a);
		logger.info(b);
		logger.info(c);
	}
}
