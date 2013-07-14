package com.ttProject.media.extra.test;

import java.nio.ByteBuffer;

import org.junit.Test;

import com.ttProject.media.extra.Bit;
import com.ttProject.media.extra.Bit1;
import com.ttProject.media.extra.Bit2;
import com.ttProject.media.extra.Bit3;
import com.ttProject.media.extra.Bit4;
import com.ttProject.media.extra.Bit5;
import com.ttProject.media.extra.Bit8;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.util.HexUtil;

public class BitTest {
	@Test
	public void test() throws Exception {
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
		Bit.bitLoader(channel,
			syncBit1, syncBit2, id, layer, protectionAbsent, profile, samplingFrequenceIndex,
			privateBit, channelConfiguration, originalFlg, home,
			copyrightIdentificationBit, copyrightIdentificationStart, frameSize1, frameSize2,
			adtsBufferFullness1, adtsBufferFullness2, noRawDataBlocksInFrame);
		channel.close();
		
		ByteBuffer buffer = Bit.bitConnector(
				syncBit1, syncBit2, id, layer, protectionAbsent, profile, samplingFrequenceIndex,
				privateBit, channelConfiguration, originalFlg, home,
				copyrightIdentificationBit, copyrightIdentificationStart, frameSize1, frameSize2,
				adtsBufferFullness1, adtsBufferFullness2, noRawDataBlocksInFrame);
		System.out.println(HexUtil.toHex(buffer.array(), true));
	}
}
