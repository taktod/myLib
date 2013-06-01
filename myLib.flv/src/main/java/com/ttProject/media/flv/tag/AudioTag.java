package com.ttProject.media.flv.tag;

import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

import com.ttProject.media.flv.CodecType;
import com.ttProject.media.flv.Tag;
import com.ttProject.nio.channels.IFileReadChannel;
import com.ttProject.util.BufferUtil;

/**
 * audioデータ
 * 知っておきたいこと
 * コーデック?
 * sampleRate? channel?
 * mediaSequenceHeader?
 * @author taktod
 */
public class AudioTag extends Tag {
	/** コーデック */
	private CodecType codec;
	/** サンプリングレート */
	private int sampleRate;
	/** チャンネル数 */
	private byte channels;
	/** aacのmediaSequenceHeaderであるかどうか */
	private boolean isMediaSequenceHeader = false;
	/** データ本体 */
	private ByteBuffer data;
	/**
	 * コンストラクタ
	 */
	public AudioTag() {
		super();
	}
	/**
	 * コンストラクタ
	 * @param size
	 * @param position
	 * @param timestamp
	 */
	public AudioTag(final int size, final int position, final int timestamp) {
		super(size, position, timestamp);
	}
	public boolean analyzeTagByte(byte tagByte) {
		// サンプルレート解析
		switch(tagByte & 0x0C) {
		case 0x0C:
			sampleRate = 44100;
			break;
		case 0x08:
			sampleRate = 22050;
			break;
		case 0x04:
			sampleRate = 11025;
			break;
		case 0x00:
			sampleRate = 5512;
			break;
		}
		if((tagByte & 0x01) == 0x01) {
			channels = 2; // ステレオ
		}
		else {
			channels = 1; // モノラル
		}
		codec = CodecType.getAudioCodecType(tagByte);
		return (codec == CodecType.AAC);
	}
	public void setCodec(CodecType codec) {
		this.codec = codec;
	}
	public void setSampleRate(int rate) {
		sampleRate = rate;
	}
	public void setChannels(byte channels) {
		this.channels = channels;
	}
	public void setMSHFlg(boolean flg) {
		isMediaSequenceHeader = flg;
	}
	/**
	 * データを登録しておく
	 * @param source
	 * @param size
	 */
	public void setData(IFileReadChannel source, int size) throws Exception {
		data = BufferUtil.safeRead(source, size);
	}
	/**
	 * データを登録しておく
	 * @param buffer
	 */
	public void setData(ByteBuffer buffer) {
		data = buffer.duplicate();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void analyze(IFileReadChannel ch, boolean atBegin) throws Exception {
		
	}
	/**
	 * 実際のタグ全体の大きさを返します。
	 * @return
	 */
	public int getRealSize() {
		if(codec == CodecType.AAC) {
			return 17 + data.remaining();
		}
		else {
			return 16 + data.remaining();
		}
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void writeTag(WritableByteChannel target) throws Exception {
		if(codec == CodecType.AAC) {
			setSize(data.remaining() + 2);
			target.write(getHeaderBuffer((byte)0x08));
			ByteBuffer buffer = ByteBuffer.allocate(2);
			byte tagByte = (byte)0xA2;
			switch((int)(sampleRate / 1000)) {
			case 44:
				tagByte |= 0x0C;
				break;
			case 22:
				tagByte |= 0x08;
				break;
			case 11:
				tagByte |= 0x04;
				break;
			case 5:
				break;
			default:
				throw new Exception("sampleRateが不正です。");
			}
			if(channels == 2) {
				tagByte |= 1;
			}
			buffer.put(tagByte);
			if(isMediaSequenceHeader) {
				buffer.put((byte)0x00);
			}
			else {
				buffer.put((byte)0x01);
			}
			buffer.flip();
			target.write(buffer);
			// 実データの書き込み
			target.write(data);
			// 終端長の書き込み
			target.write(getTailBuffer());
		}
		else {
			setSize(data.remaining() + 1);
			target.write(getHeaderBuffer((byte)0x08));
			ByteBuffer buffer = ByteBuffer.allocate(1);
			byte tagByte = (byte)0x00;
			switch(codec) {
//			case PCM: tagByte = 0x00;
			case ADPCM:		tagByte = (byte)0x12;break;
			case MP3: 		tagByte = (byte)0x22;break;
			case PCM: 		tagByte = (byte)0x32;break;
			case NELLY_16: 	tagByte = (byte)0x42;break;
			case NELLY_8: 	tagByte = (byte)0x52;break;
			case NELLY: 	tagByte = (byte)0x62;break;
			case G711_A: 	tagByte = (byte)0x72;break;
			case G711_U: 	tagByte = (byte)0x82;break;
			case RESERVED: 	tagByte = (byte)0x92;break;
//			case AAC: 		tagByte = (byte)0xA2;break;
			case SPEEX: 	tagByte = (byte)0xB2;break;
			case MP3_8: 	tagByte = (byte)0xE2;break;
			case DEVICE_SPECIFIC: tagByte = (byte)0xF2;break;

//			case 12: // 不明
//			case 13: // 未定義
			default:
				throw new RuntimeException("判定不能なコーデック");
			}
			switch((int)(sampleRate / 1000)) {
			case 44:
				tagByte |= 0x0C;
				break;
			case 22:
				tagByte |= 0x08;
				break;
			case 11:
				tagByte |= 0x04;
				break;
			case 5:
				break;
			default:
				throw new Exception("sampleRateが不正です。");
			}
			if(channels == 2) {
				tagByte |= 1;
			}
			buffer.put(tagByte);
			buffer.flip();
			target.write(buffer);
			// 実データの書き込み
			target.write(data);
			// 終端長の書き込み
			target.write(getTailBuffer());
		}
	}
	/**
	 * mediaSequenceHeaderかどうか参照
	 * @return
	 */
	public boolean isMediaSequenceHeader() {
		return isMediaSequenceHeader;
	}
	@Override
	public String toString() {
		return "audioTag:" + getTimestamp();
	}
}
