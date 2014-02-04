package com.ttProject.container.mpegts;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.BitConnector;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit12;
import com.ttProject.unit.extra.bit.Bit13;
import com.ttProject.unit.extra.bit.Bit16;
import com.ttProject.unit.extra.bit.Bit2;
import com.ttProject.unit.extra.bit.Bit4;
import com.ttProject.unit.extra.bit.Bit5;
import com.ttProject.unit.extra.bit.Bit8;
import com.ttProject.util.BufferUtil;

/**
 * programPacket
 * @author taktod
 * 
 * TODO programPacketの場合は、crc32を確認して同じだったら読み込みを実施しないという動作が必要となるとおもいます。
 * 読み込みをスキップすると動作が軽くなる。
 */
public abstract class ProgramPacket extends MpegtsPacket {
	/** ロガー */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(ProgramPacket.class);
	private Bit8 pointerField           = new Bit8(); // 0000 0000
	private Bit8 tableId                = new Bit8(); // packet固定値
	private Bit1 sectionSyntaxIndicator = new Bit1(); // 1
	private Bit1 reservedFutureUse1     = new Bit1(); // 0
	private Bit2 reserved1              = new Bit2(); // 11
	private Bit12 sectionLength         = new Bit12(); // 12bit
	private Bit16 programNumber         = new Bit16(); // 16bit
	private Bit2 reserved2              = new Bit2(); // 11
	private Bit5 versionNumber          = new Bit5(); // 00000
	private Bit1 currentNextOrder       = new Bit1(); // 1
	private Bit8 sectionNumber          = new Bit8(); // 00000000
	private Bit8 lastSectionNumber      = new Bit8(); // 00000000
	
	/** minimumLoadで保持しておくデータ */
	private ByteBuffer buffer = null;
	private boolean isLoaded = false;
	/**
	 * コンストラクタ
	 * @param syncByte
	 * @param transportErrorIndicator
	 * @param payloadUnitStartIndicator
	 * @param transportPriority
	 * @param pid
	 * @param scramblingControl
	 * @param adaptationFieldExist
	 * @param payloadFieldExist
	 * @param continuityCounter
	 */
	public ProgramPacket(Bit8 syncByte, Bit1 transportErrorIndicator,
			Bit1 payloadUnitStartIndicator, Bit1 transportPriority,
			Bit13 pid, Bit2 scramblingControl, Bit1 adaptationFieldExist,
			Bit1 payloadFieldExist, Bit4 continuityCounter) {
		super(syncByte, transportErrorIndicator, payloadUnitStartIndicator,
				transportPriority, pid, scramblingControl, adaptationFieldExist,
				payloadFieldExist, continuityCounter);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		super.minimumLoad(channel);
		// ここでは、今後読み込むであろうデータを担保しておきます
		// adaptationFieldから取得すべきデータ量を計算して、bufferにいれておきたいとおもいます。
		int bufferLength = 180; // 終端の4byteはcrc32値なので、はずしておきます。
		if(isAdaptationFieldExist()) {
			bufferLength -= (1 + getAdaptationField().getLength());
		}
		buffer = BufferUtil.safeRead(channel, bufferLength);
		// TODO ここの処理はminimumLoadからloadに変更したいとおもいます。
		// 理由はcrc32のみ先に取得するすべをつくっておきたいので
		// crc32を最速で作成する動作のみ、ここにいれたいと思います。
/*		BitLoader loader = new BitLoader(channel);
		loader.load(pointerField, tableId, sectionSyntaxIndicator,
				reservedFutureUse1, reserved1, sectionLength, programNumber,
				reserved2, versionNumber, currentNextOrder,
				sectionNumber, lastSectionNumber);
		if(isAdaptationFieldExist()) {
			super.setSize(8 + getAdaptationField().getLength() + 1 + sectionLength.get());
		}
		else {
			super.setSize(8 + sectionLength.get());
		}*/
	}
	@Override
	public void load(IReadChannel channel) throws Exception {
		BitLoader loader = new BitLoader(channel);
		loader.load(pointerField, tableId, sectionSyntaxIndicator,
				reservedFutureUse1, reserved1, sectionLength, programNumber,
				reserved2, versionNumber, currentNextOrder,
				sectionNumber, lastSectionNumber);
		if(isAdaptationFieldExist()) {
			super.setSize(8 + getAdaptationField().getLength() + 1 + sectionLength.get());
		}
		else {
			super.setSize(8 + sectionLength.get());
		}
		isLoaded = true;
	}
	/**
	 * sectionLengthの変更
	 * @param length
	 */
	protected void setSectionLength(int length) {
		// sectionLengthは後天的に変わることがあるので、設定できるようにしなければならない
		sectionLength.set(length);
		super.setSize(8 + sectionLength.get());
	}
	/**
	 * sectionLengthの参照
	 * @return
	 */
	protected int getSectionLength() {
		return sectionLength.get();
	}
	/**
	 * 動作buffer参照
	 * @return
	 */
	protected ByteBuffer getBuffer() {
		return buffer;
	}
	/**
	 * 読み込み済みか判定
	 * @return
	 */
	public boolean isLoaded() {
		return isLoaded;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected ByteBuffer getHeaderBuffer() {
		BitConnector connector = new BitConnector();
		return BufferUtil.connect(
			super.getHeaderBuffer(),
			connector.connect(
				pointerField, tableId, sectionSyntaxIndicator, reservedFutureUse1,
				reserved1, sectionLength, programNumber, reserved2, versionNumber,
				currentNextOrder, sectionNumber, lastSectionNumber
			)
		);
	}
	/**
	 * crcの値を参照します
	 * @return
	 */
	public abstract int getCrc();
	/**
	 * crcの値を算出します
	 * @param buffer
	 * @return
	 */
	protected int calculateCrc(ByteBuffer buffer) {
		// TODO この計算の仕方は正確にはただしくない。
		// 先頭にadaptationFieldがあって位置がずれている場合にきちんと動作しません。
		// 本当はpointerFieldを抜いたデータから計算すべき
		Crc32 crc32 = new Crc32();
		ByteBuffer tmpBuffer = buffer.duplicate();
		tmpBuffer.position(5);
		while(tmpBuffer.remaining() > 0) {
			crc32.update(tmpBuffer.get());
		}
		return (int)crc32.getValue();
	}
}
