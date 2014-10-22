/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.speex.test;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ttProject.frame.speex.sub.NarrowUnit;
import com.ttProject.frame.speex.sub.SubUnit;
import com.ttProject.frame.speex.sub.WideUnit;
import com.ttProject.frame.speex.type.Frame;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.BitConnector;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit2;
import com.ttProject.unit.extra.bit.Bit3;
import com.ttProject.unit.extra.bit.Bit4;
import com.ttProject.unit.extra.bit.Bit5;
import com.ttProject.unit.extra.bit.Bit6;
import com.ttProject.unit.extra.bit.Bit7;
import com.ttProject.util.HexUtil;

/**
 * load test for subunit
 * @author taktod
 */
public class SubUnitLoadTest {
	private Logger logger = Logger.getLogger(SubUnitLoadTest.class);
	@Test
	public void test() throws Exception {
		logger.info("start");
		IReadChannel channel = new ByteReadChannel(
				HexUtil.makeBuffer("2DD59A57347334C915566FD6FBABD08541C4E0D542DC9DE77F698D8FF4D85BA23A88772F7CFADE402DAD602C2C80B16D60B22162DC76160CF510F5152285920592D5BA9B077243A0A9443BCC00DDCEE7D135EEA99DB7F97D2FA9080B42D60B5B160B5AD80166D929C8E9996F55C39716A10CA1D6BCBFAE80FDF6664EB9ADB539BEBD0D5FF0E6CB321AAEABCAAA93F0B5AD60B5AC20B5AD60B5AD629D9CC978A67D22F932E6BF9D849229142FBEDBFF1CBDF57209FFFCDFFE5EF9B90B79B7F820A3B60B5AD60B5AD60B5AD60B5AD629D9CC9784015DFF7F3CDBFF3BB4AEB3A26B667247298739D03FF06FBD8A6B9532405BEC2CBA3B60B5AD60B5AD60B5AD60B5AD629D9CC9782475D0B6F3F975E11240EB5E39BC72CBB308745DE5A7FD969662BA2FF0AA3AF2EFA3B60B5AD60B5AD60B5AD60B5AD6")
//				HexUtil.makeBuffer("0E9D67FC01F27008421013BDEE4003E4E8526211")
		);
		List<SubUnit> unitList = new ArrayList<SubUnit>();
		BitLoader loader = new BitLoader(channel);
		long readBitSize = 0;
		try {
			while(true) {
				Bit1 firstBit = new Bit1();
				loader.load(firstBit);
				SubUnit unit = null;
				switch(firstBit.get()) {
				case 0:
					{
						if(unitList.size() != 0) {
							logger.info("make new frame.");
							int size = 0;
							BitConnector connector = new BitConnector();
							for(SubUnit su : unitList) {
								size += su.getBitCount();
								connector.feed(su.getBitList());
							}
							switch(size % 8) {
							case 1:connector.feed(new Bit7(0x3F));break;
							case 2:connector.feed(new Bit6(0x1F));break;
							case 3:connector.feed(new Bit5(0x0F));break;
							case 4:connector.feed(new Bit4(0x07));break;
							case 5:connector.feed(new Bit3(0x03));break;
							case 6:connector.feed(new Bit2(0x01));break;
							case 7:connector.feed(new Bit1(0x00));break;
							case 0:
							default:
								break;
							}
							Frame frame = new Frame();
							frame.minimumLoad(new ByteReadChannel(connector.connect()));
							unitList = new ArrayList<SubUnit>();
						}
						logger.info("narrowbandUnit");
						unit = new NarrowUnit();
					}
					break;
				case 1:
					{
						logger.info("widebandUnit");
						unit = new WideUnit();
					}
					break;
				default:
					throw new Exception("ouchi");
				}
				unit.load(loader);
				logger.info(unit.getBitCount());
				readBitSize += unit.getBitCount();
				unitList.add(unit);
			}
		}
		catch(Exception e) {
			logger.warn("exception is occured, maybe no more subUnit.");
			if(unitList.size() != 0) {
				logger.info("make new frame.");
				int size = 0;
				BitConnector connector = new BitConnector();
				for(SubUnit su : unitList) {
					size += su.getBitCount();
					connector.feed(su.getBitList());
				}
				switch(size % 8) {
				case 1:connector.feed(new Bit7(0x3F));break;
				case 2:connector.feed(new Bit6(0x1F));break;
				case 3:connector.feed(new Bit5(0x0F));break;
				case 4:connector.feed(new Bit4(0x07));break;
				case 5:connector.feed(new Bit3(0x03));break;
				case 6:connector.feed(new Bit2(0x01));break;
				case 7:connector.feed(new Bit1(0x00));break;
				case 0:
				default:
					break;
				}
				Frame frame = new Frame();
				frame.minimumLoad(new ByteReadChannel(connector.connect()));
			}
			logger.info(readBitSize);
		}
	}
}
