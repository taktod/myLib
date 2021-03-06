/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.unit.test;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ttProject.unit.extra.Bit;
import com.ttProject.unit.extra.BitConnector;
import com.ttProject.unit.extra.BitN;
import com.ttProject.unit.extra.bit.Bit13;
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit2;
import com.ttProject.unit.extra.bit.Bit3;
import com.ttProject.unit.extra.bit.Bit4;
import com.ttProject.unit.extra.bit.Bit5;
import com.ttProject.unit.extra.bit.Bit6;
import com.ttProject.unit.extra.bit.Bit64;
import com.ttProject.unit.extra.bit.Bit8;
import com.ttProject.unit.extra.bit.Seg;
import com.ttProject.unit.extra.bit.Ueg;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.util.HexUtil;

/**
 * bit test
 * @author taktod
 */
public class BitTest {
	/** logger */
	private Logger logger = Logger.getLogger(BitTest.class);
	/**
	 * test for read write.
	 * @throws Exception
	 */
	@Test
	public void test1() throws Exception {
		logger.info("test1");
		IReadChannel channel = new ByteReadChannel(new byte[] {
				(byte)0xFF, (byte)0xF1, 0x50, (byte)0x80, 0x02, 0x1F, (byte)0xFC
		});
		Bit4 syncBit1 = new Bit4();
		Bit8 syncBit2 = new Bit8();
		Bit1 id = new Bit1();
		Bit2 layer = new Bit2();
		Bit1 protectionAbsent = new Bit1();
		Bit2 profile = new Bit2();
		Bit4 samplingFrequenceIndex = new Bit4();
		Bit1 privateBit = new Bit1();
		Bit3 channelConfiguration = new Bit3();
		Bit1 originalFlg = new Bit1();
		Bit1 home = new Bit1();
		Bit1 copyrightIdentificationBit = new Bit1();
		Bit1 copyrightIdentificationStart = new Bit1();
		Bit5 frameSize1 = new Bit5();
		Bit8 frameSize2 = new Bit8();
		Bit3 adtsBufferFullness1 = new Bit3();
		Bit8 adtsBufferFullness2 = new Bit8();
		Bit2 noRawDataBlocksInFrame = new Bit2();
		BitLoader bitLoader = new BitLoader(channel);
		bitLoader.load(
			syncBit1, syncBit2, id, layer, protectionAbsent, profile, samplingFrequenceIndex,
			privateBit, channelConfiguration, originalFlg, home,
			copyrightIdentificationBit, copyrightIdentificationStart, frameSize1, frameSize2,
			adtsBufferFullness1, adtsBufferFullness2, noRawDataBlocksInFrame);
		channel.close();
		
		BitConnector bitConnector = new BitConnector();
		ByteBuffer buffer = bitConnector.connect(
				syncBit1, syncBit2, id, layer, protectionAbsent, profile, samplingFrequenceIndex,
				privateBit, channelConfiguration, originalFlg, home,
				copyrightIdentificationBit, copyrightIdentificationStart, frameSize1, frameSize2,
				adtsBufferFullness1, adtsBufferFullness2, noRawDataBlocksInFrame);
		logger.info(HexUtil.toHex(buffer.array(), true));
	}
	/**
	 * test for ext golomb(for h264)
	 * @throws Exception
	 */
	@Test
	public void test2() throws Exception {
		logger.info("test2");
		IReadChannel channel = new ByteReadChannel(new byte[] {
				(byte)0xF7
		});
		Bit1 a = new Bit1();
		Bit2 b = new Bit2();
		Bit1 c = new Bit1();
		Ueg ueg = new Ueg();
		Seg seg = new Seg();
		BitLoader bitLoader = new BitLoader(channel);
		bitLoader.load(a, b, c, ueg, seg);
		logger.info(a);
		logger.info(b);
		logger.info(c);
		logger.info(ueg);
		logger.info(seg);
	}
	/**
	 * test for extra bit read.
	 * @throws Exception
	 */
	@Test
	public void test3() throws Exception {
		logger.info("test3");
		IReadChannel channel = new ByteReadChannel(new byte[]{
			0x12, 0x34, 0x56, 0x78	
		});
		BitLoader bitLoader = new BitLoader(channel);
		Bit1 bit1 = new Bit1();
		Bit2 bit2 = new Bit2();
		Bit6 bit5 = new Bit6();
		bitLoader.load(bit1, bit2, bit5);
		logger.info(bit1);
		logger.info(bit2);
		logger.info(bitLoader.getExtraBit());
		logger.info(channel.position());
		logger.info(channel.size());
	}
	/**
	 * test for signed exp golomb
	 * @throws Exception
	 */
	@Test
	public void test4() throws Exception {
		logger.info("test4");
		Seg seg = new Seg();
		logger.info(seg.toString());
		seg.set(-5);
		logger.info(seg.toString());
	}
	@Test
	public void test5() throws Exception {
		logger.info("test5");
		Bit b1 = new Bit1(1);
		Bit seg = new Seg();
		seg.set(-5);
		logger.info(b1);
		logger.info(seg);
		BitConnector connector = new BitConnector();
		ByteBuffer buffer = connector.connect(b1, seg);
		logger.info(HexUtil.toHex(buffer));
	}
	@Test
	public void test6() throws Exception {
		logger.info("test6");
		Bit3 bit3 = new Bit3(3);
		Bit13 bit13 = new Bit13(1025);
		logger.info(bit3);
		logger.info(bit13);
		BitConnector connector = new BitConnector();
		ByteBuffer buffer = connector.connect(bit3, bit13);
		logger.info(HexUtil.toHex(buffer));
		BitLoader loader = new BitLoader(new ByteReadChannel(buffer));
		Bit4 bit4 = new Bit4();
		BitN bitN = new BitN(new Bit4(), new Bit8());
		loader.load(bit4, bitN);
		logger.info(bit4);
		logger.info(bitN);
	}
	@Test
	public void test7() throws Exception {
		logger.info("test7");
		BitConnector connector = new BitConnector();
		List<Bit> bits = new ArrayList<Bit>();
		bits.add(new Bit1(1));
		bits.add(new Bit1(0));
		bits.add(new Bit1(1));
		bits.add(new Bit1(0));
		bits.add(new Bit4(1));
		connector.feed(bits);
		connector.feed(new Bit2(), new Bit4(4), new Bit1(), new Bit1());
		logger.info(HexUtil.toHex(connector.connect(), true));
	}
	@Test
	public void test8() throws Exception {
		logger.info("test8");
		BitConnector connector = new BitConnector();
		logger.info(HexUtil.toHex(connector.connect((Bit)null)));
	}
	@Test
	public void test9() throws Exception {
		logger.info("test9");
		Bit64 bit = new Bit64();
		bit.setLong(0xFF00000000L);
		logger.info(bit);
		bit.setLong(0xFFFFFFFFL);
		logger.info(bit);
	}
}
