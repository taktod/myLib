package com.ttProject.media.aac;

import java.nio.ByteBuffer;
import java.util.List;

import com.ttProject.media.Manager;
import com.ttProject.media.aac.frame.Aac;
import com.ttProject.media.extra.Bit;
import com.ttProject.media.extra.Bit1;
import com.ttProject.media.extra.Bit2;
import com.ttProject.media.extra.Bit3;
import com.ttProject.media.extra.Bit4;
import com.ttProject.media.extra.Bit5;
import com.ttProject.media.extra.Bit8;
import com.ttProject.nio.channels.IReadChannel;

/**
 * ADTS方式であるとして、Aacのデータを解析します。
 * @see http://blog-imgs-18-origin.fc2.com/n/a/n/nanncyatte/aacfileheader.png
 * @author taktod
 */
public class AacManager extends Manager<Frame> {
	@Override
	public List<Frame> getUnits(ByteBuffer data) throws Exception {
		return null;
	}
	@Override
	public Frame getUnit(IReadChannel source) throws Exception {
		// frameUnitを解析します。
		// headerは7バイトで構成されているので、7バイト存在しない場合は処理できません。
		if(source.size() - source.position() < 7) {
			return null;
		}
		int position = source.position();
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
		Bit.bitLoader(source,
			syncBit1, syncBit2, id, layer, protectionAbsent, profile, samplingFrequenceIndex,
			privateBit, channelConfiguration, originalFlg, home,
			copyrightIdentificationBit, copyrightIdentificationStart, frameSize1, frameSize2,
			adtsBufferFullness1, adtsBufferFullness2, noRawDataBlocksInFrame);
		int size = (frameSize1.get() << 8) + frameSize2.get();
		return new Aac(position, size, id, layer, protectionAbsent, profile, samplingFrequenceIndex, privateBit, channelConfiguration, originalFlg, home, copyrightIdentificationBit, copyrightIdentificationStart, size, (adtsBufferFullness1.get() << 8) + adtsBufferFullness2.get(), noRawDataBlocksInFrame);
	}
}
