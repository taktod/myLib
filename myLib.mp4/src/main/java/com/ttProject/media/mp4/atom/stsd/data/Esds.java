package com.ttProject.media.mp4.atom.stsd.data;

import java.nio.ByteBuffer;

import com.ttProject.util.BufferUtil;
import com.ttProject.media.mp4.Atom;
import com.ttProject.media.mp4.IAtomAnalyzer;
import com.ttProject.nio.channels.IReadChannel;

/**
 * mp4のstsdの内部データのさらに奥のデータ
 * 音声用らしい
 * こちらはaacのmediaSequenceHeaderを保持しているが、解析する必要あり。
 * データ的には音声onlyではないかもしれない
 * @author taktod
 */
@SuppressWarnings("unused")
public class Esds extends Atom {
	private int unknown;
	// 内部のタグのデータ
	private final int ES_TAG = 0x03;
	private final int DECODER_CONFIG = 0x04;
	private final int DECODER_SPECIFIC = 0x05;
	private final int SL_CONFIG = 0x06;
	private byte objectType;
	private byte[] sequenceHeader;
	public byte[] getSequenceHeader() {
		return sequenceHeader;
	}
	/**
	 * esdsに設定されているobjectTypeのyteデータを参照する。
	 * TODO このデータを見れば、コーデックがmp3かaacかとかわかるはず。
	 * @return
	 */
	public byte getObjectType() {
		return objectType;
	}
	public Esds(int size, int position) {
		super(Esds.class.getSimpleName().toLowerCase(), size, position);
	}
	@Override
	public void analyze(IReadChannel ch, IAtomAnalyzer analyzer) throws Exception {
		// とりあえず解析しよう。
		ch.position(getPosition() + 8);
		ByteBuffer buffer = BufferUtil.safeRead(ch, getSize() - 8);
		unknown = buffer.getInt();
		while(buffer.remaining() > 0) {
			analyzeTag(buffer);
		}
	}
	private void analyzeTag(ByteBuffer buffer) throws Exception {
		if(buffer.remaining() == 0) {
			return;
		}
		// tagを調べる。
		byte tag = buffer.get();
		int size = getSize(buffer);
		byte flags;
		switch(tag) {
		case ES_TAG:
			// 次のデータが可変長変数値
			// 次の2バイトはES_ID
			short esId = buffer.getShort();
			flags = buffer.get();
			if(flags != 0) {
				throw new Exception("ES_TAG flags is unknown.");
			}
			// ここで元の場所に戻る。
			analyzeTag(buffer);
			break;
		case DECODER_CONFIG:
			// 1バイトオブジェクトタイプ
			objectType = buffer.get();
			System.out.println("objType:" + Integer.toHexString(objectType));
			switch(objectType & 0xFF) {
			case 0x01: // system v1
				break;
			case 0x02: // system v2
				break;
			case 0x20: // mpeg4-video
				break;
			case 0x21: // mpeg-4 avc sps
				break;
			case 0x22: // mpeg-4 avc pps
				break;
			case 0x40: // mpeg-4 audio(aac?)
				break;
			case 0x60: // mpeg-2 simple video
				break;
			case 0x61: // mpeg-2 main video
				break;
			case 0x62: // mpeg-2 snr video
				break;
			case 0x63: // mpeg-2 special video
				break;
			case 0x64: // mpeg-2 high video
				break;
			case 0x65: // mpeg-2 4:2:2 video
				break;
			case 0x66: // mpeg-4 adts main
				break;
			case 0x67: // mpeg-4 adts low complexity
				break;
			case 0x68: // mpeg-4 adts scalable sampling rate
				break;
			case 0x69: // mpeg-2 adts
				break;
			case 0x6A: // mpeg-1 video
				break;
			case 0x6B: // mpeg-1 adts
				break;
			case 0x6C: // jpeg video
				break;
			case 0xC0: // private audio
				break;
			case 0xD0: // private video
				break;
			case 0xE0: // 16-bit PCM le audio
				break;
			case 0xE1: // vorbis audio
				break;
			case 0xE2: // dolby v3 ac3 audio
				break;
			case 0xE3: // alaw audio
				break;
			case 0xE4: // mulaw audio
				break;
			case 0xE5: // adpcm audio
				break;
			case 0xE6: // 16-bit pcm big endian audio
				break;
			case 0xF0: // Y'CbCr 4:2:0(YV12 video)
				break;
			case 0xF1: // H264 video
				break;
			case 0xF2: // H263 video
				break;
			case 0xF3: // H261 video
				break;
			default: // unknwon
				break;
			}
			// 次の1バイト flags
			int data = buffer.getInt();
			flags = (byte)((data >> 24) & 0xFF);
			int bufferSize = (data & 0x00FFFFFF);
			int maxBitRate = buffer.getInt();
			int avgBitRate = buffer.getInt();
			// ここで元の場所に戻る。
			analyzeTag(buffer);
			break;
		case DECODER_SPECIFIC:
			// サイズを取得してそのサイズ分がMediaSequenceHeaderの情報になる。(aacの場合)
			byte[] msh = new byte[size];
			buffer.get(msh);
			// このデータがmediaSequenceHeaderのデータ(flvにするにはこれが欲しい)
			sequenceHeader = msh;
			analyzeTag(buffer);
			break;
		case SL_CONFIG:
			byte[] conf = new byte[size];
			buffer.get(conf);
			break;
		}
	}
	private int getSize(ByteBuffer buffer) {
		int size = 0;
		byte b = 0;
		do {
			b = buffer.get();
			size = size * 0x80 + (b & 0x7F);
		}while((b & 0x80) != 0x00);
		return size;
	}
}
