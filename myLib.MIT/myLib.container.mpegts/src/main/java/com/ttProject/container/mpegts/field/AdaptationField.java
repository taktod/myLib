package com.ttProject.container.mpegts.field;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.Bit;
import com.ttProject.unit.extra.BitConnector;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit31;
import com.ttProject.unit.extra.bit.Bit33;
import com.ttProject.unit.extra.bit.Bit6;
import com.ttProject.unit.extra.bit.Bit8;
import com.ttProject.unit.extra.bit.Bit9;

/**
 * adaptationFieldの内容保持
 * @author taktod
 */
public class AdaptationField {
	private Bit8 adaptationFieldLength = new Bit8(); // lengthのみも成立しうる
	private Bit1 discontinuityIndicator = null; // 0
	private Bit1 randomAccessIndicator = null; // aacの先頭だけ、たってる？ (aacのみでも同様)(h264のキーフレームもたってるっぽい)
	private Bit1 elementaryStreamPriorityIndicator = null; // 0
	private Bit1 pcrFlag = null;
	private Bit1 opcrFlag = null; // originalPcr(コピーするときにつかうらしい。) // 0
	private Bit1 splicingPointFlag = null; // 0
	private Bit1 transportPrivateDataFlag = null; // 0
	private Bit1 adaptationFieldExtensionFlag = null; // 0
	// pcr
	private Bit33 pcrBase = null;
	private Bit6 pcrPadding = null;
	private Bit9 pcrExtension = null;
	// opcr
	private Bit33 opcrBase = null;
	private Bit6 opcrPadding = null;
	private Bit9 opcrExtension = null;
	public boolean hasPcr() {
		return pcrFlag.get() == 1;
	}
	public long getPcrBase() {
		BitConnector connector = new BitConnector();
		return connector.connect(new Bit31(), pcrBase).getLong();
	}
	public long getOpcrBase() {
		BitConnector connector = new BitConnector();
		return connector.connect(new Bit31(), opcrBase).getLong();
	}
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
	public void setPcrBase(long base) throws Exception {
		ByteBuffer buffer = ByteBuffer.allocate(8);
		buffer.putLong(base);
		buffer.flip();
		BitLoader loader = new BitLoader(new ByteReadChannel(buffer));
		loader.load(new Bit31(), pcrBase);
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
		BitLoader bitLoader = new BitLoader(channel);
		bitLoader.load(adaptationFieldLength);
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
		bitLoader = new BitLoader(channel);
		bitLoader.load(discontinuityIndicator, randomAccessIndicator,
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
			pcrBase = new Bit33();
			pcrPadding = new Bit6();
			pcrExtension = new Bit9();
			bitLoader = new BitLoader(channel);
			bitLoader.load(pcrBase, pcrPadding, pcrExtension);
			size -= 6;
		}
		if(opcrFlag.get() != 0x00) {
			// pcrと同じっぽいので実装しとく。
			opcrBase = new Bit33();
			opcrPadding = new Bit6();
			opcrExtension = new Bit9();
			bitLoader = new BitLoader(channel);
			bitLoader.load(opcrBase, opcrPadding, opcrExtension);
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
		if(length == 0) {
			return list;
		}
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
			list.add(pcrBase);
			list.add(pcrPadding);
			list.add(pcrExtension);
			length -= 6;
		}
		if(opcrFlag.get() != 0x00) {
			list.add(opcrBase);
			list.add(opcrPadding);
			list.add(opcrExtension);
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
				data.append("[pcrBase:").append(Long.toHexString(getPcrBase()))
					.append("(").append(getPcrBase() / 90000f).append("sec)");
				data.append(" pcrPadding:").append(pcrPadding);
				data.append(" pcrExtension:").append(pcrExtension);
				data.append("]");
			}
			if(opcrFlag.get() != 0x00) {
				data.append("[opcrBase:").append(Long.toHexString(getOpcrBase()))
					.append("(").append(getOpcrBase() / 90000f).append("sec)");
				data.append(" opcrPadding:").append(opcrPadding);
				data.append(" opcrExtension:").append(opcrExtension);
				data.append("]");
			}
		}
		return data.toString();
	}
}
