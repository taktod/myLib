/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.h264.test;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit2;
import com.ttProject.unit.extra.bit.Bit32;
import com.ttProject.unit.extra.bit.Bit4;
import com.ttProject.unit.extra.bit.Bit5;
import com.ttProject.unit.extra.bit.Bit6;
import com.ttProject.unit.extra.bit.Bit8;
import com.ttProject.unit.extra.bit.Ueg;
import com.ttProject.util.HexUtil;

/**
 * h264のseiの読み込み動作テスト
 * これをいれて、ptsをきちんと調整するようにしておきたいねぇ・・・
 * @author taktod
 */
public class SupplementalEnhancementInformationTest {
	/** ロガー */
	private Logger logger = Logger.getLogger(SupplementalEnhancementInformationTest.class);
	/**
	 * とりあえず読み込み動作テストを書いておきたい。
	 * @throws Exception
	 */
	@Test
	public void test() throws Exception {
		IReadChannel target = new ByteReadChannel(HexUtil.makeBuffer("06001180003E8480000003000003003E84800000030040010D00010000030090804D3DA90000030080"));
		BitLoader loader = new BitLoader(target);
		loader.setEmulationPreventionFlg(true);
		// 06001180003E8480000003000003003E84800000030040010D00010000030090804D3DA90000030080
		// 06001180003E848000000000003E848000000040010D000100000090804D3DA900000080 ← 00 00 03の03の部分撤去したらこうなる
		
		// こうなる
		// 0011 80003E848000000000003E848000000040 ← buffering_period
		// 010D000100000090804D3DA9000000 ← pic_timing
		
		// 0011 80001BF30000000000001BF30000000040
		// 010D000100000090806D3DA9000000
		// buffering_periodの読み込み動作
		target = new ByteReadChannel(HexUtil.makeBuffer("80003E848000000000003E848000000040"));
		target.close();
		// delay:32009とdelayoffset:0
		target = new ByteReadChannel(HexUtil.makeBuffer("80001BF30000000000001BF30000000040"));
		// delay:14310とdelayoffset:0
		loader = new BitLoader(target);
		Ueg spsId = new Ueg();
		loader.load(spsId);
		Bit32 initialCpbRemovalDelay = new Bit32();
		Bit32 initialCpbRemovalDelayOffset = new Bit32();
		loader.load(initialCpbRemovalDelay, initialCpbRemovalDelayOffset);
		logger.info(initialCpbRemovalDelay.get());
		logger.info(initialCpbRemovalDelayOffset.get());

		initialCpbRemovalDelay = new Bit32();
		initialCpbRemovalDelayOffset = new Bit32();
		loader.load(initialCpbRemovalDelay, initialCpbRemovalDelayOffset);
		logger.info("initialCpbRemovalDelay" + initialCpbRemovalDelay.get());
		logger.info(initialCpbRemovalDelayOffset.get());
		logger.info(target.position() + " / " + target.size());
		target.close();
	}
	@Test
	public void timeperiodTest() throws Exception {
		logger.info("テスト開始");
		IReadChannel target = new ByteReadChannel(HexUtil.makeBuffer("000380000090806D7DA9000000"));
		BitLoader loader = new BitLoader(target);
		Bit32 cpbRemovalDelay = new Bit32();
		Bit32 dpbOutputDelay = new Bit32();
		loader.load(cpbRemovalDelay, dpbOutputDelay);
		Bit4 picStrict = new Bit4();
		loader.load(picStrict);
		logger.info(cpbRemovalDelay.get());
		logger.info(dpbOutputDelay.get());
		logger.info(picStrict.get());
		Bit1 clockTimestampFlag = new Bit1();
		loader.load(clockTimestampFlag);
		logger.info(clockTimestampFlag.get());
		if(clockTimestampFlag.get() == 1) {
			Bit2 ctType = new Bit2();
			Bit1 nuitFieldBasedFlag = new Bit1();
			Bit5 countingType = new Bit5();
			Bit1 fullTimestampFlag = new Bit1();
			Bit1 discontinuityFlag = new Bit1();
			Bit1 cntDroppedFlag = new Bit1();
			Bit8 nFrames = new Bit8();
			loader.load(ctType, nuitFieldBasedFlag, countingType, fullTimestampFlag, discontinuityFlag, cntDroppedFlag, nFrames);
			logger.info(fullTimestampFlag.get());
			if(fullTimestampFlag.get() == 1) {
				Bit6 secondsVal = new Bit6();
				Bit6 minutesVal = new Bit6();
				Bit5 hoursVal = new Bit5();
				loader.load(secondsVal, minutesVal, hoursVal);
			}
			else {
				Bit1 secondFlag = new Bit1();
				loader.load(secondFlag);
				logger.info(secondFlag.get());
			}
			Bit8 timeOffset = new Bit8();
			loader.load(timeOffset);
			logger.info(timeOffset.get());
		}
		logger.info(target.position() + " / " + target.size());
		
		target = new ByteReadChannel(HexUtil.makeBuffer("000400000090807D7DA9000000"));
		loader = new BitLoader(target);
		cpbRemovalDelay = new Bit32();
		dpbOutputDelay = new Bit32();
		loader.load(cpbRemovalDelay, dpbOutputDelay);
		picStrict = new Bit4();
		loader.load(picStrict);
		logger.info(cpbRemovalDelay.get());
		logger.info(dpbOutputDelay.get());
		logger.info(picStrict.get());
		clockTimestampFlag = new Bit1();
		loader.load(clockTimestampFlag);
		logger.info(clockTimestampFlag.get());
	}
	/**
	 * こっちもよみこみたいね。
	 * @throws Exception
	 */
//	@Test
	public void test2() throws Exception {
		IReadChannel target = new ByteReadChannel(HexUtil.makeBuffer("0605FFFF18DC45E9BDE6D948B7962CD820D923EEEF78323634202D20636F72652036372072313136324D2066376266636661202D20482E3236342F4D5045472D342041564320636F646563202D20436F70796C65667420323030332D32303039202D20687474703A2F2F7777772E766964656F6C616E2E6F72672F783236342E68746D6C202D206F7074696F6E733A2063616261633D31207265663D33206465626C6F636B3D313A2D313A2D3120616E616C7973653D3078333A3078313333206D653D756D68207375626D653D36207073795F72643D312E303A302E30206D697865645F7265663D31206D655F72616E67653D3332206368726F6D615F6D653D31207472656C6C69733D32203878386463743D312063716D3D3020646561647A6F6E653D32312C3131206368726F6D615F71705F6F66667365743D2D3220746872656164733D33206E723D3020646563696D6174653D31206D626166663D3020626672616D65733D3320625F707972616D69643D3120625F61646170743D3120625F626961733D30206469726563743D33207770726564623D31206B6579696E743D353030206B6579696E745F6D696E3D3530207363656E656375743D34302072633D637266206372663D33302E302071636F6D703D302E36302071706D696E3D352071706D61783D3330207170737465703D342069705F726174696F3D312E34302070625F726174696F3D312E33302061713D313A312E30300080"));
		// 0605FFFF18DC45E9BDE6D948B7962CD820D923EEEF78323634202D20636F72652036372072313136324D20663762
		// [] seiのtypeデータ
		//   [] 内容のデータ 0x05なのでuser_data_unregistered
		//     [    ] 0xFF + 0xFF + 0x18 = 0x216 534byte(00 00 03を調整した後のデータでのサイズになります。)
		//           [                              ] uuid_iso_iec_11578 単なるID
		//                                           [以下データ内容
		BitLoader loader = new BitLoader(target);
		loader.setEmulationPreventionFlg(true);
		Bit8 b = new Bit8();
		loader.load(b);
/*
0: buffering_period
1: pic_timing
2: pan_scan_rect
3: filler_payload
4: user_data_registered_itu_t_t35
5: user_data_unregistered
6: recovery_point
7: dec_ref_pic_marking_repetition
8: spare_pic
9: scene_info

 */
	}
}
