package com.ttProject.media.mp3.frame;

import java.nio.ByteBuffer;

import com.ttProject.media.IAudioData;
import com.ttProject.media.extra.Bit;
import com.ttProject.media.extra.Bit1;
import com.ttProject.media.extra.Bit2;
import com.ttProject.media.extra.Bit3;
import com.ttProject.media.extra.Bit4;
import com.ttProject.media.extra.Bit8;
import com.ttProject.media.mp3.Frame;
import com.ttProject.media.mp3.IFrameAnalyzer;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.util.BufferUtil;

/**
 * mp3のデータフレーム
 * @author taktod
 */
public class Mp3 extends Frame implements IAudioData {
	private final short syncBit = (short)0x07FF;
	private Bit2 mpegVersion;
	private Bit2 layer;
	private Bit1 protectionBit;
	private Bit4 bitrateIndex;
	private Bit2 samplingRateIndex;
	private Bit1 paddingBit;
	private Bit1 privateBit;
	private Bit2 channelMode;
	private Bit2 modeExtension;
	private Bit1 copyRight;
	private Bit1 originalFlg;
	private Bit2 emphasis;
	/** 保持している実データ部 */
	private ByteBuffer data;
	private final int bitrateIndexV1L1[] = {
		-1, 32, 64, 96, 128, 160, 192, 224, 256, 288, 320, 352, 384, 416, 448, -1
	};
	private final int bitrateIndexV1L2[] = {
		-1, 32, 48, 56,  64,  80,  96, 112, 128, 160, 192, 224, 256, 320, 384, -1
	};
	private final int bitrateIndexV1L3[] = {
		-1, 32, 40, 48,  56,  64,  80,  96, 112, 128, 160, 192, 224, 256, 320, -1
	};
	private final int bitrateIndexV2L1[] = {
		-1, 32, 48, 56,  64,  80,  96, 112, 128, 144, 160, 176, 192, 224, 256, -1
	};
	private final int bitrateIndexV2L23[] = {
		-1,  8, 16, 24,  32,  40,  48,  56,  64,  80,  96, 112, 128, 144, 160, -1
	};
	private final float sampleRateTable[][] = {
		{11.025f, 12.0f,  8.0f}, // mpeg 2.5
		{-1.0f, -1.0f, -1.0f},   // reserved
		{22.05f,  24.0f, 16.0f}, // mpeg 2
		{44.1f,   48.0f, 32.0f}  // mpeg 1
	};
	/**
	 * サンプリングレートを応答します。
	 * @return
	 */
	public float getSamplingRate() {
		return sampleRateTable[mpegVersion.get()][samplingRateIndex.get()];
	}
	@Override
	public int getSampleRate() {
		return (int)(getSamplingRate() *1000);
	}
	/**
	 * フレームあたりのサンプル数を応答します。
	 * @return
	 */
	@Override
	public int getSampleNum() {
		if(layer.get() == 3) { // layer1
			return 384;
		}
		else if(layer.get() == 2) { // layer2
			return 1152;
		}
		else if(layer.get() == 1) { // layer3
			if(mpegVersion.get() == 3) { // version1
				return 1152;
			}
			else {
				return 576;
			}
		}
		return 0;
	}
	// videoData用の拡張動作
	private long pts = 0;
	private long dts = 0;
	private double timebase = 0.001;
	public void setPts(long pts) {
		this.pts = pts;
	}
	public long getPts() {
		return pts;
	}
	public void setDts(long dts) {
		this.dts = dts;
	}
	public long getDts() {
		return dts;
	}
	public void setTimebase(double timebase) {
		this.timebase = timebase;
	}
	public double getTimebase() {
		return timebase;
	}
	@Override
	public ByteBuffer getRawData() throws Exception {
		return getBuffer();
	}
	/**
	 * チャンネルモードを応答します。
	 * @return
	 */
	public int getChannelMode() {
		return channelMode.get();
	}
	/**
	 * チャンネル数を応答します。
	 * @return
	 */
	public int getChannels() {
		return channelMode.get() == 3 ? 1 : 2;
	}
	public int getBitrate() {
		if(mpegVersion.get() == 0 || mpegVersion.get() == 2) { // 2.5と2の場合
			if(layer.get() == 3) { // layer1
				return bitrateIndexV2L1[bitrateIndex.get()];
			}
			else if(layer.get() == 2 || layer.get() == 1) { // layer2,3
				return bitrateIndexV2L23[bitrateIndex.get()];
			}
		}
		if(mpegVersion.get() == 3) { // 1の場合
			if(layer.get() == 1) { // layer3
				return bitrateIndexV1L3[bitrateIndex.get()];
			}
			else if(layer.get() == 2) { // layer2
				return bitrateIndexV1L2[bitrateIndex.get()];
			}
			else if(layer.get() == 3) { // layer1
				return bitrateIndexV1L1[bitrateIndex.get()];
			}
		}
		return -1;
	}
	private int getRealSize() {
		if(layer.get() == 3) { // layer1
			return (int)Math.floor((12 * getBitrate() / getSamplingRate() + paddingBit.get()) * 4);
		}
		else if(layer.get() == 2) { // layer2
			return (int)Math.floor(144 * getBitrate() / getSamplingRate() + paddingBit.get());
		}
		else if(layer.get() == 1) { // layer3
			if(mpegVersion.get() == 3) { // version1の場合
				return (int)Math.floor(144 * getBitrate() / getSamplingRate() + paddingBit.get());
			}
			else {
				return (int)Math.floor(72 * getBitrate() / getSamplingRate() + paddingBit.get());
			}
		}
		return -1;
	}
	/**
	 * コンストラクタ
	 * TODO 3つつくる。設定で変わるデータをベースにつくるもの。なにもなしでの処理、ファイルからの読み込みデータをベースにした処理
	 */
	public Mp3() {
		super(0, 0);
	}
	public Mp3(int position,
			Bit2 mpegVersion, Bit2 layer, Bit1 protectionBit, Bit4 bitrateIndex,
			Bit2 samplingRateIndex, Bit1 paddingBit, Bit1 privateBit, Bit2 channelMode,
			Bit2 modeExtension, Bit1 copyRight, Bit1 originalFlg, Bit2 emphasis) {
		super(position, 0);
		this.mpegVersion = mpegVersion;
		this.layer = layer;
		this.protectionBit = protectionBit;
		this.bitrateIndex = bitrateIndex;
		this.samplingRateIndex = samplingRateIndex;
		this.paddingBit = paddingBit;
		this.privateBit = privateBit;
		this.channelMode = channelMode;
		this.modeExtension = modeExtension;
		this.copyRight = copyRight;
		this.originalFlg = originalFlg;
		this.emphasis = emphasis;
		setSize(getRealSize());
	}
	public void setData(ByteBuffer buffer) {
		this.data = buffer.duplicate();
	}
	public ByteBuffer getBuffer() throws Exception {
		ByteBuffer buffer = ByteBuffer.allocate(getSize());
		buffer.put(Bit.bitConnector(
				new Bit3((byte)(syncBit >>> 8)), new Bit8((byte)(syncBit)),
				mpegVersion, layer, protectionBit, bitrateIndex, samplingRateIndex, paddingBit,
				privateBit, channelMode, modeExtension, copyRight, originalFlg, emphasis));
		buffer.put(data);
		buffer.flip();
		return buffer;
	}
	@Override
	public void analyze(IReadChannel ch, IFrameAnalyzer analyzer)
			throws Exception {
		ch.position(getPosition() + 4);
		data = BufferUtil.safeRead(ch, getSize() - 4);
	}
	@Override
	public String toString() {
		StringBuilder data = new StringBuilder("mp3Frame:");
		data.append(" ");
		data.append(getSamplingRate());
		data.append("kHz");
		data.append(" pos:");
		data.append(Integer.toHexString(getPosition()));
		data.append(" size:");
		data.append(Integer.toHexString(getSize()));
		return data.toString();
	}
}
