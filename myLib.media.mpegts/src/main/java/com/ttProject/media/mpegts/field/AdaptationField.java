package com.ttProject.media.mpegts.field;

import java.util.ArrayList;
import java.util.List;

import com.ttProject.media.extra.Bit;
import com.ttProject.media.extra.Bit1;
import com.ttProject.media.extra.Bit6;
import com.ttProject.media.extra.Bit8;
import com.ttProject.nio.channels.IReadChannel;

/**
 * adaptationFieldの内容保持
 * ちなみにPCRは100msec以内に一度更新してやる必要があるらしいので注意が必要。
 * @author taktod
 */
public class AdaptationField {
	private Bit8 adaptationFieldLength;
	private Bit1 discontinuityIndicator; // 0
	private Bit1 randomAccessIndicator; // aacの先頭だけ、たってる？ (aacのみでも同様)(h264のキーフレームもたってるっぽい)
	private Bit1 elementaryStreamPriorityIndicator; // 0
	private Bit1 pcrFlag;
	private Bit1 opcrFlag; // originalPcr(コピーするときにつかうらしい。) // 0
	private Bit1 splicingPointFlag; // 0
	private Bit1 transportPrivateDataFlag; // 0
	private Bit1 adaptationFieldExtensionFlag; // 0
	
	// PCR用
	private long pcrBase; // 33bit 90KHz表示
	private Bit6 pcrPadding; // 111111
	private short pcrExtension; // 9bit 27MHz // 0
	
	// OPCR用
	private long opcrBase; // 33bit 90KHz表示
	private Bit6 opcrPadding;
	private short opcrExtension; // 9bit 27MHz

	public AdaptationField() {
		adaptationFieldLength = new Bit8(0);
		discontinuityIndicator = new Bit1(0);
		randomAccessIndicator = new Bit1(0);
		elementaryStreamPriorityIndicator = new Bit1(0);
		pcrFlag = new Bit1(0);
		opcrFlag = new Bit1(0);
		splicingPointFlag = new Bit1(0);
		transportPrivateDataFlag = new Bit1(0);
		adaptationFieldExtensionFlag = new Bit1(0);
	}
	public void setPcrBase(long base) {
		pcrBase = base;
	}
	public void setRandomAccessIndicator(int flg) {
		// adaptationFieldの長さが存在しない場合は1に変更する必要あり。
		if(adaptationFieldLength.get() == 0) {
			adaptationFieldLength.set(1);
		}
		randomAccessIndicator = new Bit1(flg);
	}
	// pcr opcr spliceCountdown stuffingBytes等々・・・
	public void analyze(IReadChannel channel) throws Exception {
		// とりあえずlengthをみておく。
		adaptationFieldLength = new Bit8();
		Bit.bitLoader(channel, adaptationFieldLength);
		if(adaptationFieldLength.get() == 0x00) {
			return;
		}
		int size = adaptationFieldLength.get();
		discontinuityIndicator = new Bit1();
		randomAccessIndicator = new Bit1();
		elementaryStreamPriorityIndicator = new Bit1();
		pcrFlag = new Bit1();
		opcrFlag = new Bit1();
		splicingPointFlag = new Bit1();
		transportPrivateDataFlag = new Bit1();
		adaptationFieldExtensionFlag = new Bit1();
		Bit.bitLoader(channel, discontinuityIndicator, randomAccessIndicator,
				elementaryStreamPriorityIndicator, pcrFlag, opcrFlag, splicingPointFlag,
				transportPrivateDataFlag, adaptationFieldExtensionFlag);
		size --;
		// 他のデータがある場合は読み込んでいく必要あり。
		if(pcrFlag.get() != 0x00) {
			// pcrがある場合
			// とりあえず、つづく、33bit + 6Bit + 9Bitからデータがなるみたいです。
			// 33bitの部分を90000で割るとおよそのデータ長がとれるみたい。
			// はじめの33bitは90kHzでの表示、最終の9bitは27MHzでの表示となるみたいです。
			// 中間の6bitはpaddingBit
			// とりあえずおおよそのデータがわかればよろしい感じなので、データはとっておきますが、33bitの部分からだけでデータを取得しておきます。
			Bit1 pcrBase_1 = new Bit1();
			Bit8 pcrBase_2 = new Bit8();
			Bit8 pcrBase_3 = new Bit8();
			Bit8 pcrBase_4 = new Bit8();
			Bit8 pcrBase_5 = new Bit8();
			pcrPadding = new Bit6();
			Bit1 pcrExtension_1 = new Bit1();
			Bit8 pcrExtension_2 = new Bit8();
			Bit.bitLoader(channel, pcrBase_1, pcrBase_2, pcrBase_3, pcrBase_4, pcrBase_5,
					pcrPadding, pcrExtension_1, pcrExtension_2);
			pcrBase = (((long)pcrBase_1.get()) << 32) | (((long)pcrBase_2.get()) << 24) | (pcrBase_3.get() << 16) | (pcrBase_4.get() << 8) | pcrBase_5.get();
			pcrExtension = (short)((pcrExtension_1.get() << 8) | pcrExtension_2.get());
			size -= 6;
		}
		if(opcrFlag.get() != 0x00) {
			// pcrと同じっぽいので実装しとく。
			Bit1 opcrBase_1 = new Bit1();
			Bit8 opcrBase_2 = new Bit8();
			Bit8 opcrBase_3 = new Bit8();
			Bit8 opcrBase_4 = new Bit8();
			Bit8 opcrBase_5 = new Bit8();
			opcrPadding = new Bit6();
			Bit1 opcrExtension_1 = new Bit1();
			Bit8 opcrExtension_2 = new Bit8();
			Bit.bitLoader(channel, opcrBase_1, opcrBase_2, opcrBase_3, opcrBase_4, opcrBase_5,
					opcrPadding, opcrExtension_1, opcrExtension_2);
			opcrBase = (((long)opcrBase_1.get()) << 32) | (((long)opcrBase_2.get()) << 24) | (opcrBase_3.get() << 16) | (opcrBase_4.get() << 8) | opcrBase_5.get();
			opcrExtension = (short)((opcrExtension_1.get() << 8) | opcrExtension_2.get());
			size -= 6;
		}
		if(splicingPointFlag.get() != 0x00) {
			throw new Exception("splicingPointの解析は未実装です。");
		}
		if(transportPrivateDataFlag.get() != 0x00) {
			throw new Exception("transportPrivateDataの解析は未実装です。");
		}
		if(adaptationFieldExtensionFlag.get() != 0x00) {
			throw new Exception("adaptationFieldExtensionの解析は未実装です。");
		}
		if(size != 0) {
			// 何のフラグもなくてすべてffで埋められているっぽい。
			// とりあえずスルーする必要があるっぽいが
			channel.position(channel.position() + size); // あいている部分はスキップしてやる必要あり。
		}
	}
	/**
	 * 長さを変更する。
	 * @param length
	 */
	public void setLength(int length) {
		adaptationFieldLength = new Bit8(length);
	}
	/**
	 * 長さを参照する。
	 * @return
	 */
	public int getLength() {
		return adaptationFieldLength.get();
	}
	public List<Bit> getBits() {
		List<Bit> list = new ArrayList<Bit>();
		int length = adaptationFieldLength.get();
		list.add(adaptationFieldLength);
		list.add(discontinuityIndicator);
		list.add(randomAccessIndicator);
		list.add(elementaryStreamPriorityIndicator);
		list.add(pcrFlag);
		list.add(opcrFlag);
		list.add(splicingPointFlag);
		list.add(transportPrivateDataFlag);
		list.add(adaptationFieldExtensionFlag);
		length --;
		if(pcrFlag.get() != 0x00) {
			list.add(new Bit1((int)(pcrBase >>> 32)));
			list.add(new Bit8((int)(pcrBase >>> 24)));
			list.add(new Bit8((int)(pcrBase >>> 16)));
			list.add(new Bit8((int)(pcrBase >>> 8)));
			list.add(new Bit8((int)(pcrBase)));
			list.add(pcrPadding);
			list.add(new Bit1(pcrExtension >>> 8));
			list.add(new Bit8(pcrExtension));
			length -= 6;
		}
		if(opcrFlag.get() != 0x00) {
			list.add(new Bit1((int)(opcrBase >>> 32)));
			list.add(new Bit8((int)(opcrBase >>> 24)));
			list.add(new Bit8((int)(opcrBase >>> 16)));
			list.add(new Bit8((int)(opcrBase >>> 8)));
			list.add(new Bit8((int)(opcrBase)));
			list.add(opcrPadding);
			list.add(new Bit1(opcrExtension >>> 8));
			list.add(new Bit8(opcrExtension));
			length -= 6;
		}
		for(int i = 0;i < length;i ++) {
			list.add(new Bit8((byte)0xFF));
		}
		return list;
	}
	public int getRandomAccessIndicator() {
		return randomAccessIndicator.get();
	}
	@Override
	public String toString() {
		StringBuilder data = new StringBuilder();
		data.append(" ");
		data.append("adaptationField:");
		data.append(" afl:").append(Integer.toHexString(adaptationFieldLength.get()));
		if(adaptationFieldLength.get() != 0) {
			data.append(" di:").append(discontinuityIndicator);
			data.append(" rai:").append(randomAccessIndicator);
			data.append(" espi:").append(elementaryStreamPriorityIndicator);
			data.append(" pf:").append(pcrFlag);
			data.append(" of:").append(opcrFlag);
			data.append(" spf:").append(splicingPointFlag);
			data.append(" tpdf:").append(transportPrivateDataFlag);
			data.append(" afef:").append(adaptationFieldExtensionFlag);
			if(pcrFlag.get() != 0x00) {
				data.append("[pcrBase:").append(Long.toHexString(pcrBase))
					.append("(").append(pcrBase / 90000f).append("sec)");
				data.append(" pcrPadding:").append(pcrPadding);
				data.append(" pcrExtension:").append(pcrExtension);
				data.append("]");
			}
			if(opcrFlag.get() != 0x00) {
				data.append("[opcrBase:").append(Long.toHexString(opcrBase))
					.append("(").append(opcrBase / 90000f).append("sec)");
				data.append(" opcrPadding:").append(opcrPadding);
				data.append(" opcrExtension:").append(opcrExtension);
				data.append("]");
			}
		}
		return data.toString();
	}
	/**
	 * 
	 * @return
	 */
	public String dump() {
		StringBuilder data = new StringBuilder("adaptationField:");
		data.append(" afl:").append(Integer.toHexString(adaptationFieldLength.get()));
		if(adaptationFieldLength.get() != 0) {
			data.append(" di:").append(discontinuityIndicator);
			data.append(" rai:").append(randomAccessIndicator);
			data.append(" espi:").append(elementaryStreamPriorityIndicator);
			data.append(" pf:").append(pcrFlag);
			data.append(" of:").append(opcrFlag);
			data.append(" spf:").append(splicingPointFlag);
			data.append(" tpdf:").append(transportPrivateDataFlag);
			data.append(" afef:").append(adaptationFieldExtensionFlag);
			if(pcrFlag.get() != 0x00) {
				data.append("[pcrBase:").append(Long.toHexString(pcrBase))
					.append("(").append(pcrBase / 90000f).append("sec)");
				data.append(" pcrPadding:").append(pcrPadding);
				data.append(" pcrExtension:").append(pcrExtension);
				data.append("]");
			}
			if(opcrFlag.get() != 0x00) {
				data.append("[opcrBase:").append(Long.toHexString(opcrBase))
					.append("(").append(opcrBase / 90000f).append("sec)");
				data.append(" opcrPadding:").append(opcrPadding);
				data.append(" opcrExtension:").append(opcrExtension);
				data.append("]");
			}
		}
		return data.toString();
	}
}
