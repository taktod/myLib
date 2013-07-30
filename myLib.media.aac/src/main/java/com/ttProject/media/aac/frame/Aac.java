package com.ttProject.media.aac.frame;

import java.nio.ByteBuffer;

import com.ttProject.media.aac.DecoderSpecificInfo;
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
 * prifile 01
 * sampleRateIndex 0100
 * channelConfiguration 010
 * 
 * (profile + 1) << 3 | sampleRateIndex >> 1
 * (sampleRateIndex & 0x01) << 7 | channelConfig << 3
 * 
 * 0x12 0x10　mshの部分
 * 0001 0010 0x12
 * 0001 0000 0x10
 * おぉっ
 * 
 * その後のflvタグは次のようになっている。
 * 08 size[3byte] timestamp[4byte(転地あり)] trackId[3byte(0埋め)] コーデックタイプ、チャンネル、サンプリングレートフラグ(1byte)
 * mshFlag(00:msh 01:通常フレーム) adtsのヘッダ部をのぞいたデータ
 * tailSize[4byte]
 * @author taktod
 */
public class Aac extends Frame {
	private final short syncWork = (short)0x0FFF;
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
		// デフォルト設定
		id = new Bit1(); // 0:Mpeg4
		layer = new Bit2(); // 常に0
		protectionAbsent = new Bit1(1); // 一応保護あり
		profile = new Bit2(0); // main profileにしておく(mediaSequenceHeaderの情報から拾える)
		samplingFrequenceIndex = new Bit4(4); // 44.100kHzとしておく。あとで変更すべき
		privateBit = new Bit1(); // 1固定
		channelConfiguration = new Bit3(2); // チャンネル数とりあえずステレオにしておく。
		originalFlg = new Bit1(); // オリジナルデータ
		home = new Bit1(); // わからん。とりあえず0
		copyrightIdentificationBit = new Bit1(); // 0
		copyrightIdentificationStart = new Bit1(); // 0
		frameSize = size; // データサイズ
		adtsBufferFullness = 0x7FF; // とりあえずVBR
		noRawDataBlocksInFrame = new Bit2(); // 0でOKみたい
	}
	/**
	 * コンストラクタwith specificInfo
	 * @param size
	 * @param specificInfo
	 */
	public Aac(int size, DecoderSpecificInfo specificInfo) {
		this(size);
		profile = new Bit2(specificInfo.getObjectType());
		samplingFrequenceIndex = new Bit4(specificInfo.getFrequenctIndex());
		channelConfiguration = new Bit3(specificInfo.getChannelConfiguration());
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
		data = BufferUtil.safeRead(ch, getSize() - 7);
	}
	/**
	 * データを設定する。
	 * @param buffer 読み込みモードのbyteBuffer
	 */
	public void setData(ByteBuffer buffer) {
		this.data = buffer.duplicate();
		// TODO このタイミングでframeSizeとsizeを更新する必要あり。
		setSize(7 + data.remaining());
		this.frameSize = 7 + data.remaining();
	}
	/**
	 * adtsのデータとして、データを応答する(aacファイルやmpegtsで使う)
	 * @return
	 */
	public ByteBuffer getBuffer() throws Exception {
		ByteBuffer buffer = ByteBuffer.allocate(getSize());
		// 先頭のデータもつくっておく。
		buffer.put(Bit.bitConnector(
				new Bit4((syncWork >>> 8)), new Bit8((syncWork)), id, layer, protectionAbsent, profile, samplingFrequenceIndex,
				privateBit, channelConfiguration, originalFlg, home,
				copyrightIdentificationBit, copyrightIdentificationStart, new Bit5((frameSize >>> 8)), new Bit8((frameSize & 0xFF)),
				new Bit3((adtsBufferFullness >>> 8)), new Bit8((adtsBufferFullness & 0xFF)), noRawDataBlocksInFrame));
		data.position(0); // もどしておく？
		// 実データ部をつくっておく。
		buffer.put(data);
		buffer.flip();
		return buffer; // 応答
	}
	/**
	 * aacの実データ部のみ応答する。(mp4とかで使う)
	 * @return
	 */
	public ByteBuffer getDataBuffer() {
		return data.duplicate();
	}
	public float getSamplingRate() {
		return sampleRateTable[samplingFrequenceIndex.get()];
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
