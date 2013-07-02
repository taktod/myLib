package com.ttProject.media.mp3;

import java.nio.ByteBuffer;

import com.ttProject.media.mp3.frame.ID3;
import com.ttProject.media.mp3.frame.Mp3;
import com.ttProject.nio.channels.IFileReadChannel;
import com.ttProject.util.BufferUtil;

/**
 * mp3の解析動作
 * @author taktod
 */
public class FrameAnalyzer implements IFrameAnalyzer {
	private int frameCount = 0;
	@Override
	public Frame analyze(IFileReadChannel ch) throws Exception {
		// ３バイトの確認を実行します。
		if(ch.size() - ch.position() < 3) {
			return null;
		}
		int position = ch.position();
		ByteBuffer buffer = BufferUtil.safeRead(ch, 3);
		byte[] data = new byte[3];
		buffer.get(data);
		// ID3V2の場合
		if(data[0] == 'I'
		&& data[1] == 'D'
		&& data[2] == '3') {
			buffer = BufferUtil.safeRead(ch, 7);
			short version = buffer.getShort();
			data = new byte[5];
			buffer.get(data);
			return new ID3(position, ((data[1] & 0x7F) << 21) + ((data[2] & 0x7F) << 14) + ((data[3] & 0x7F) << 7) + (data[4] & 0x7F) + 10, version, data[0]);
		}
		// ID3V1の場合(終端にくるデータなので、とりあえず無視しておく)
		else if(data[0] == 'T'
		&& data[1] == 'A'
		&& data[2] == 'G') {
				throw new RuntimeException("ID3v1はサポートしていないです。");
		}
		else if(data[0] == (byte)0xFF && (data[1] & 0xE0) == 0xE0) {
			byte data3 = BufferUtil.safeRead(ch, 1).get();
			byte mpegVersion = 0;
			byte layer = 0;
			int bitrate;
			float samplingRate;
			byte paddingBit;
			byte channelMode;
			// mpegVersion
			switch((data[1] & 0x18) >>> 3) {
			case 0:mpegVersion = 2;break;	// mpeg2.5(unofficial)
			case 1:throw new Exception("予約済みversionId、詳細は不明");
			case 2:mpegVersion = 1;break;	// mpeg2
			case 3:mpegVersion = 0;break;	// mpeg1
			}
			// layer
			switch((data[1] & 0x06) >> 1) {
			case 0:throw new Exception("予約済みLayer。処理不能");
			case 1:layer = 2;break;	// layer3
			case 2:layer = 1;break;	// layer2
			case 3:layer = 0;break;	// layer1
			}
			// protectionbit(無視)
			// bitrate
			bitrate = getBitrate((data[2] & 0xF0) >>> 4, mpegVersion, layer);
			// samplingRate
			samplingRate = sampleRateTable[mpegVersion][(data[2] & 0x0C) >>> 2];
			// paddingBit
			paddingBit = (byte)((data[2] & 0x02) >>> 1);
			// private Bit(無視)
			// channelMode
			channelMode = (byte)((data3 & 0xC0) >>> 6);
			// データの長さ
			int size = getSize(mpegVersion, layer, bitrate, samplingRate, paddingBit); // 4バイトのheader部をぬいてあります。
			return new Mp3(position, size, mpegVersion, layer, bitrate, samplingRate, paddingBit, channelMode, frameCount ++);
		}
		return null;
	}
	/**
	 * フレームカウンターの初期化
	 */
	public void clearFrameCount() {
		frameCount = 0;
	}
	/**
	 * ビットレートを取得する
	 * @param index
	 * @return
	 */
	private static int getBitrate(int index, byte mpegVersion, byte layer) {
		if(mpegVersion == 2 || mpegVersion == 1) { // 2.5と2の場合
			if(layer == 0) { // layer1
				return bitrateIndexV2L1[index];
			}
			else if(layer == 1 || layer == 2) { // layer2,3
				return bitrateIndexV2L23[index];
			}
		}
		if(mpegVersion == 0) { // 1の場合
			if(layer == 2) { // layer3
				return bitrateIndexV1L3[index];
			}
			else if(layer == 1) { // layer2
				return bitrateIndexV1L2[index];
			}
			else if(layer == 0) { // layer1
				return bitrateIndexV1L1[index];
			}
		}
		return -1;
	}
	private static final int bitrateIndexV1L1[] = {
		-1, 32, 64, 96, 128, 160, 192, 224, 256, 288, 320, 352, 384, 416, 448, -1
    };
	private static final int bitrateIndexV1L2[] = {
		-1, 32, 48, 56,  64,  80,  96, 112, 128, 160, 192, 224, 256, 320, 384, -1
    };
	private static final int bitrateIndexV1L3[] = {
		-1, 32, 40, 48,  56,  64,  80,  96, 112, 128, 160, 192, 224, 256, 320, -1
    };
	private static final int bitrateIndexV2L1[] = {
		-1, 32, 48, 56,  64,  80,  96, 112, 128, 144, 160, 176, 192, 224, 256, -1
    };
	private static final int bitrateIndexV2L23[] = {
		-1,  8, 16, 24,  32,  40,  48,  56,  64,  80,  96, 112, 128, 144, 160, -1
    };
	/**
	 * 変換テーブル
	 */
	private static final float sampleRateTable[][] = {
		{44.1f,   48.0f, 32.0f}, // mpeg 1
		{22.05f,  24.0f, 16.0f}, // mpeg 2
		{11.025f, 12.0f,  8.0f}  // mpeg 2.5
	};
	/**
	 * パケットのサイズを計算する。
	 * TODO layer1の動作は自信なし
	 * @return
	 */
	private static int getSize(byte mpegVersion, byte layer, int bitrate, float samplingRate, int paddingBit) {
		if(layer == 0) { // layer1
			return (int)Math.floor((12 * bitrate / samplingRate + paddingBit) * 4);
		}
		else if(layer == 1) { // layer2
			return (int)Math.floor(144 * bitrate / samplingRate + paddingBit);
		}
		else if(layer == 2) { // layer3
			if(mpegVersion == 0) {
				return (int)Math.floor(144 * bitrate / samplingRate + paddingBit);
			}
			else {
				return (int)Math.floor(72 * bitrate / samplingRate + paddingBit);
			}
		}
		return -1;
	}
}
