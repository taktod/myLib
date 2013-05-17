package com.ttProject.packet.mp3;

import java.nio.ByteBuffer;

import com.ttProject.packet.MediaPacket;

/**
 * mp3の実体処理
 * @author taktod
 */
public abstract class Mp3Packet extends MediaPacket {
	/** 書き込みする状態かどうかフラグ(id3v2のタグは、スキップするので、そのときにこのフラグを確認します。) */
	private boolean writeMode = false;
	/** 処理データ量 */
	private int targetLength = 0;
	/** mp3の設定データ */
	private int mpegVersion;
	private int layer;
	private int bitrate;
	private float samplingRate;
	private int paddingBit;
	@SuppressWarnings("unused")
	private int channelMode; // これだけ特に参照していない。
	/** 開始位置 */
	private int startPos = -1;
	/** 参照用のマネージャーオブジェクト */
	private final Mp3PacketManager manager;
	/**
	 * コンストラクタ
	 * @param manager
	 */
	public Mp3Packet(Mp3PacketManager manager) {
		this.manager = manager;
	}
	/**
	 * 解析を実施する。
	 */
	@Override
	public boolean analize(ByteBuffer buffer) {
		while(buffer.remaining() >= 4) {
			int position = buffer.position();
			if(targetLength != 0) {
				// 処理途上な場合の処理
				// 指定された量のデータがない場合は、かけるだけ書いて、応答する。
				if(writeMode) {
					// 書き込み要求
					int length = targetLength > buffer.remaining() ? buffer.remaining() : targetLength;
					byte[] data = new byte[length];
					buffer.get(data);
					getBuffer(length).put(data);
					targetLength -= length;
				}
				else {
					// スキップ要求
					if(targetLength > buffer.remaining()) {
						targetLength -= buffer.remaining();
						// targetLengthの大きくて今回の処理でおわらない場合
						buffer.position(position + buffer.remaining());
					}
					else {
						// targetLengthの方が小さい場合単にスキップすればよい。
						buffer.position(position + targetLength);
						targetLength = 0;
					}
				}
			}
			else {
				// 4バイト以上あることを保証してあります。
				byte[] readData = new byte[4];
				buffer.get(readData);
				// この4バイトの中身がヘッダーであることを期待します。
				if(readData[0] == 'I'
				&& readData[1] == 'D'
				&& readData[2] == '3') {
					buffer.position(position);
					// id3v2のタグであることが期待されます。
					// このタイミングで、10バイト読み込めることを期待します。
					if(buffer.remaining() < 10) {
						return false;
					}
					readData = new byte[10];
					buffer.get(readData);
					int id3v2Length = ((readData[6] & 0x7F) << 21) + ((readData[7] & 0x7F) << 14) + ((readData[8] & 0x7F) << 7) + (readData[9] & 0x7F);
					// 書き込みをせずに、対象量を読み飛ばします。
					targetLength = id3v2Length;
					writeMode = false;
				}
				else if(readData[0] == 'T'
					  && readData[1] == 'A'
					  && readData[2] == 'G') {
					// id3v1のタグの場合はファイルの終端である可能性が高いので、処理を終わらせます。
//					System.out.println("id3v1タグ");
					// manager側にデータをおわった旨の登録が必要だと思われます。
					// ここは例外ではなく、おわったという処理にすべきかも
					throw new RuntimeException("終端が見えたので処理を中止します。");
				}
				else {
					// 通常のタグ
					if((readData[0] & 0xFF) != 0xFF || (readData[1] & 0xE0) != 0xE0) {
						// headerの確認bitがおかしいです。
						throw new RuntimeException("headerのsyncビットがおかしいです。");
					}
					// mpegAudio versionID
					switch((readData[1] & 0x18) >>> 3) {
					default:
						// 解釈できない。
						throw new RuntimeException("解釈できないデータがversionIdにきました。");
					case 0:
						// mpeg2.5
						mpegVersion = 2;
						break;
					case 1:
						// reserved
						throw new RuntimeException("予約されているversionIdです。処理不能");
					case 2:
						// mpeg2
						mpegVersion = 1;
						break;
					case 3:
						// mpeg1
						mpegVersion = 0;
						break;
					}
					// layer
					switch((readData[1] & 0x06) >> 1) {
					default:
						throw new RuntimeException("解釈できないデータがlayerにきました。");
					case 0:
						throw new RuntimeException("予約されているlayerです。処理不能");
					case 1:
						layer = 2;
						break;
					case 2:
						layer = 1;
						break;
					case 3:
						layer = 0;
						break;
					}
					// protectionbit(興味なし)
					// bitrateIndex
					bitrate = getBitrate((readData[2] & 0xF0) >>> 4);
					
					// samplingRate
					samplingRate = sampleRateTable[mpegVersion][(readData[2] & 0x0C) >>> 2];
					// paddingBit
					paddingBit = (readData[2] & 0x02) >>> 1;
					// privateBit skip
					// channelMode
					channelMode = (readData[3] & 0xC0) >>> 6;

					// durationを更新
					setDuration(getTime() / 1000 - manager.getPassedTime());
					int passedTime = getPassedTime() / 1000;
					// この方法だと、分割につかう秒数がだめな感じになってしまう。(端数がちょっとずつずれていく。)
					// TODO 分割に利用するdurationのデータ取得だけ、なんとかしておかないとだめ。
					if(passedTime >= manager.getDuration()) { // 経過時間が5秒すぎている場合は、次のパケットにすすむ(分割を実行する。)
						// 現在時刻から、manager上の経過時刻をひいてdurationを求める
						manager.addPassedTime(getDuration());
						buffer.position(position);
						return true;
					}

					// 書き込みを実行するサイズを指定しておく。
					targetLength = getSize() - 4;
					// frameCountをインクリメントしておく。
					// 現在の経過時間を計算しておく。
					manager.addFrameCount();
					getBuffer(4).put(readData);
					writeMode = true;
				}
			}
		}
		return false;
	}
	/**
	 * 経過時刻を取得する。
	 * @return
	 */
	private int getPassedTime() {
		if(startPos == -1) {
			startPos = getTime();
		}
		return getTime() - startPos;
	}
	/**
	 * パケットから現在時刻を取得する。
	 * @return
	 */
	private int getTime() {
		if(layer == 0) { // layer1
			return (int)Math.floor(manager.getFrameCount() * 384 / samplingRate);
		}
		else if(layer == 1) { // layer2
			return (int)Math.floor(manager.getFrameCount() * 1152 / samplingRate);
		}
		else if(layer == 2) { // layer3
			if(mpegVersion == 0) {
				return (int)Math.floor(manager.getFrameCount() * 1152 / samplingRate);
			}
			else {
				return (int)Math.floor(manager.getFrameCount() * 576 / samplingRate);
			}
		}
		return -1;
	}
	/**
	 * パケットのサイズを計算する。
	 * TODO layer1の動作は自信なし
	 * @return
	 */
	private int getSize() {
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
	/**
	 * 変換テーブル
	 */
	public static final float sampleRateTable[][] = {
		{44.1f,   48.0f, 32.0f}, // mpeg 1
		{22.05f,  24.0f, 16.0f}, // mpeg 2
		{11.025f, 12.0f,  8.0f}  // mpeg 2.5
	};
	/**
	 * ビットレートを取得する
	 * @param index
	 * @return
	 */
	public int getBitrate(int index) {
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
	public static final int bitrateIndexV1L1[] = {
		-1, 32, 64, 96, 128, 160, 192, 224, 256, 288, 320, 352, 384, 416, 448, -1
    };
	public static final int bitrateIndexV1L2[] = {
		-1, 32, 48, 56,  64,  80,  96, 112, 128, 160, 192, 224, 256, 320, 384, -1
    };
	public static final int bitrateIndexV1L3[] = {
		-1, 32, 40, 48,  56,  64,  80,  96, 112, 128, 160, 192, 224, 256, 320, -1
    };
	public static final int bitrateIndexV2L1[] = {
		-1, 32, 48, 56,  64,  80,  96, 112, 128, 144, 160, 176, 192, 224, 256, -1
    };
	public static final int bitrateIndexV2L23[] = {
		-1,  8, 16, 24,  32,  40,  48,  56,  64,  80,  96, 112, 128, 144, 160, -1
    };
}
