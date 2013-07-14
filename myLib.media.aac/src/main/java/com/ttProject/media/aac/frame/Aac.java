package com.ttProject.media.aac.frame;

import java.nio.ByteBuffer;

import com.ttProject.media.aac.Frame;
import com.ttProject.media.aac.IFrameAnalyzer;
import com.ttProject.media.extra.Bit;
import com.ttProject.media.extra.Bit1;
import com.ttProject.media.extra.Bit2;
import com.ttProject.media.extra.Bit3;
import com.ttProject.media.extra.Bit4;
import com.ttProject.media.extra.Bit5;
import com.ttProject.media.extra.Bit8;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.util.BufferUtil;

/**
 * Aacの内部データ
 * サンプルデータ例
 * FF F1 50 80 02 1F FC
 * FF F1 50 80 40 9F FC
 * @author taktod
 */
public class Aac extends Frame {
	private final short syncWork = (short)0xFFF0;
	private Bit1 id;
	private Bit2 layer;
	private Bit1 protectionAbsent;
	private Bit2 profile;
	private Bit4 samplingFrequenceIndex;
	private Bit1 privateBit;
	private Bit3 channelConfiguration;
	private Bit1 originalFlg;
	private Bit1 home;
	private Bit1 copyrightIdentificationBit;
	private Bit1 copyrightIdentificationStart;
	private int frameSize; // 13bit
	private int adtsBufferFullness; // 11bit
	private Bit2 noRawDataBlocksInFrame;
	/** 保持している実データ部 */
	private ByteBuffer data;
	/** 周波数テーブル */
	private static float [] sampleRateTable = {96.0f, 88.2f, 64.0f, 48.0f, 44.1f, 32.0f, 24.0f, 22.05f, 16.0f, 12.0f, 11.025f, 8.0f};
	/**
	 * コンストラクタ
	 * @param channelCount チャンネル数
	 * @param sampleFrequenceRate サンプリング周波数(kHz単位)
	 * @param data 実データ
	 */
	public Aac(int channelCount, float sampleFrequenceRate, ByteBuffer data) {
		this(data.remaining());
		channelConfiguration.set(channelCount);
		for(int i = 0;i < sampleRateTable.length; i ++) {
			if(sampleFrequenceRate == sampleRateTable[i]) {
				samplingFrequenceIndex.set(i);
				break;
			}
		}
		this.data = data.duplicate();
	}
	/**
	 * コンストラクタ
	 * @param size サイズ情報
	 */
	public Aac(int size) {
		super(0, size);
		id = new Bit1();
		layer = new Bit2();
		protectionAbsent = new Bit1(1);
		profile = new Bit2(0);
		samplingFrequenceIndex = new Bit4(4);
		privateBit = new Bit1();
		channelConfiguration = new Bit3(2);
		originalFlg = new Bit1();
		home = new Bit1();
		copyrightIdentificationBit = new Bit1();
		copyrightIdentificationStart = new Bit1();
		frameSize = size;
		adtsBufferFullness = 0x7FF; // とりあえずVBR
		noRawDataBlocksInFrame = new Bit2();
	}
	/**
	 * コンストラクタwith細かい情報
	 * @param position
	 * @param size
	 * @param id
	 * @param layer
	 * @param protectionAbsent
	 * @param profile
	 * @param samplingFrequenceIndex
	 * @param privateBit
	 * @param channelConfiguration
	 * @param originalFlg
	 * @param home
	 * @param copyrightIdentificationBit
	 * @param copyrightIdentificationStart
	 * @param frameSize
	 * @param adtsBufferFullness
	 * @param noRawDataBlocksInFrame
	 */
	public Aac(int position, int size, 
			Bit1 id, Bit2 layer, Bit1 protectionAbsent,
			Bit2 profile, Bit4 samplingFrequenceIndex,
			Bit1 privateBit, Bit3 channelConfiguration,
			Bit1 originalFlg, Bit1 home, 
			Bit1 copyrightIdentificationBit, Bit1 copyrightIdentificationStart,
			int frameSize, int adtsBufferFullness,
			Bit2 noRawDataBlocksInFrame) {
		super(position, size);
		this.id = id;
		this.layer = layer;
		this.protectionAbsent = protectionAbsent;
		this.profile = profile;
		this.samplingFrequenceIndex = samplingFrequenceIndex;
		this.privateBit = privateBit;
		this.channelConfiguration = channelConfiguration;
		this.originalFlg = originalFlg;
		this.home = home;
		this.copyrightIdentificationBit = copyrightIdentificationBit;
		this.copyrightIdentificationStart = copyrightIdentificationStart;
		this.frameSize = frameSize;
		this.adtsBufferFullness = adtsBufferFullness;
		this.noRawDataBlocksInFrame = noRawDataBlocksInFrame;
	}
	/**
	 * 内部データの解析動作
	 */
	@Override
	public void analyze(IReadChannel ch, IFrameAnalyzer analyzer)
			throws Exception {
		// chからデータを読み込んでdataにいれておく。
		ch.position(getPosition() + 7);
		BufferUtil.safeRead(ch, getSize() - 7);
	}
	/**
	 * データを設定する。
	 * @param buffer 読み込みモードのbyteBuffer
	 */
	public void setData(ByteBuffer buffer) {
		this.data = buffer.duplicate();
	}
	/**
	 * adtsのデータとして、データを応答する(aacファイルやmpegtsで使う)
	 * @return
	 */
	public ByteBuffer getBuffer() throws Exception {
		ByteBuffer buffer = ByteBuffer.allocate(getSize());
		// 先頭のデータもつくっておく。
		buffer.put(Bit.bitConnector(
				new Bit4((byte)(syncWork >>> 8)), new Bit8((byte)(syncWork & 0xFF)), id, layer, protectionAbsent, profile, samplingFrequenceIndex,
				privateBit, channelConfiguration, originalFlg, home,
				copyrightIdentificationBit, copyrightIdentificationStart, new Bit5((byte)(frameSize >>> 8)), new Bit8((byte)(frameSize & 0xFF)),
				new Bit3((byte)(adtsBufferFullness >>> 8)), new Bit8((byte)(adtsBufferFullness & 0xFF)), noRawDataBlocksInFrame));
		// 実データ部をつくっておく。
		buffer.put(data);
		return buffer; // 応答
	}
	/**
	 * aacの実データ部のみ応答する。(mp4とかで使う)
	 * @return
	 */
	public ByteBuffer getDataBuffer() {
		return data.duplicate();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder data = new StringBuilder("aacFrame:");
		data.append(" profile:");
		switch(profile.get()) {
		case 0:
			data.append("Main");
			break;
		case 1:
			data.append("LC");
			break;
		case 2:
			data.append("SSR");
			break;
		default:
		case 3:
			data.append("reserved");
			break;
		}
		// samplingRate
		data.append(" ");
		data.append(sampleRateTable[samplingFrequenceIndex.get()]);
		data.append("kHz");
		data.append(" pos:");
		data.append(Integer.toHexString(getPosition()));
		data.append(" size:");
		data.append(Integer.toHexString(getSize()));
		return data.toString();
	}
}
