package com.ttProject.media.flv.tag;

import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

import com.ttProject.media.flv.CodecType;
import com.ttProject.media.flv.Tag;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.util.BufferUtil;

/**
 * audioデータ
 * @author taktod
 */
public class AudioTag extends Tag implements Cloneable {
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
	 * コンストラクタ(データがファイルにない場合の処理)
	 */
	public AudioTag() {
		super();
	}
	/**
	 * コンストラクタ(データがそもそもファイルにある場合の処理)
	 * @param size
	 * @param position
	 * @param timestamp
	 */
	public AudioTag(final int position, final int size, final int timestamp) {
		super(position, size, timestamp);
	}
	/**
	 * メディアタグ用のbyteデータから必要な情報を抽出します。
	 * 12バイト目にあるやつ
	 * @param tagByte
	 * @return
	 */
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
	public byte getTagByte() throws Exception {
		byte tagByte = 0x00;
		// codec判定
		switch(codec) {
		case PCM: 				tagByte = (byte)0x00;break;
		case ADPCM:				tagByte = (byte)0x12;break;
		case MP3: 				tagByte = (byte)0x22;break;
		case LPCM: 				tagByte = (byte)0x32;break; // pcmの場合は2がはいっているのはおかしいかも？
		case NELLY_16: 			tagByte = (byte)0x42;break;
		case NELLY_8: 			tagByte = (byte)0x52;break;
		case NELLY: 			tagByte = (byte)0x62;break;
		case G711_A: 			tagByte = (byte)0x72;break;
		case G711_U: 			tagByte = (byte)0x82;break;
		case RESERVED: 			tagByte = (byte)0x92;break;
		case SPEEX: 			tagByte = (byte)0xB2;break;
		case MP3_8: 			tagByte = (byte)0xE2;break;
		case DEVICE_SPECIFIC:	tagByte = (byte)0xF2;break;

		case AAC: 				tagByte = (byte)0xA2;
			if(data != null) {
				setSize(data.remaining() + 2 + 15); // aacの場合はサイズがちょっとかわるので、上書きしておく。
			}
			break;

//		case 12: // 不明
//		case 13: // 未定義
		default:
			throw new RuntimeException("判定不能なコーデック");
		}
		// サンプリングレート
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
		// サウンドチャンネル指定
		if(channels == 2) {
			tagByte |= 1;
		}
		return tagByte;
	}
	/**
	 * コーデックを設定
	 * @param codec
	 */
	public void setCodec(CodecType codec) {
		this.codec = codec;
	}
	/**
	 * コーデックを参照
	 * @return
	 */
	public CodecType getCodec() {
		return codec;
	}
	/**
	 * サンプルレートを設定
	 * @param rate
	 */
	public void setSampleRate(int rate) {
		sampleRate = rate;
	}
	/**
	 * サンプルレートを参照
	 * @return
	 */
	public int getSampleRate() {
		return sampleRate;
	}
	/**
	 * 音声チャンネル数を設定
	 * @param channels
	 */
	public void setChannels(byte channels) {
		this.channels = channels;
	}
	/**
	 * チャンネル数を参照
	 * @return
	 */
	public byte getChannels() {
		return channels;
	}
	/**
	 * mshであるか設定する
	 * @param flg
	 */
	public void setMSHFlg(boolean flg) {
		isMediaSequenceHeader = flg;
	}
	/**
	 * データを登録しておく(メディアデータの本当の部分のみ)
	 * @param source
	 * @param size
	 */
	public void setData(IReadChannel source, int size) throws Exception {
		data = BufferUtil.safeRead(source, size);
	}
	/**
	 * データを登録しておく(メディアデータの本当の部分のみ)
	 * @param buffer
	 */
	public void setRawData(ByteBuffer buffer) {
		data = buffer.duplicate();
	}
	public ByteBuffer getRawData() {
		return data.duplicate();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void analyze(IReadChannel ch, boolean atBegin) throws Exception {
		// ファイルからの解析はあとでつくっておく。
		super.analyze(ch, atBegin);
		// 実データを読み込んでおく。
		ch.position(getPosition() + 11);
		// 1バイト読み込んで、コーデックを解析しておく。
		analyzeTagByte(BufferUtil.safeRead(ch, 1).get());
		int dataSize = getSize() - 16;
		// AACの場合はさらに1バイト読み込んでおく。
		if(codec == CodecType.AAC) {
			isMediaSequenceHeader = (BufferUtil.safeRead(ch, 1).get() != 0x01);
			dataSize --;
		}
		// 実データ部を読み込む
		setData(ch, dataSize);
		// tailを読み込む
		if(BufferUtil.safeRead(ch, 4).getInt() != getSize() - 4) {
			throw new Exception("tailByteの長さが狂ってます");
		}
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void writeTag(WritableByteChannel target) throws Exception {
		target.write(getBuffer());
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ByteBuffer getBuffer() throws Exception {
		// データのベース位置を更新します。
		data.position(0);
		// tagを作成します。
//		byte tagByte = 0x00;
		// デフォルトサイズの更新
		if(codec == CodecType.AAC) {
			setSize(data.remaining() + 2 + 15);
		}
		else {
			setSize(data.remaining() + 1 + 15);
		}
		// codec判定
/*		switch(codec) {
//		case PCM: tagByte = 0x00;
		case ADPCM:				tagByte = (byte)0x12;break;
		case MP3: 				tagByte = (byte)0x22;break;
		case PCM: 				tagByte = (byte)0x32;break; // pcmの場合は2がはいっているのはおかしいかも？
		case NELLY_16: 			tagByte = (byte)0x42;break;
		case NELLY_8: 			tagByte = (byte)0x52;break;
		case NELLY: 			tagByte = (byte)0x62;break;
		case G711_A: 			tagByte = (byte)0x72;break;
		case G711_U: 			tagByte = (byte)0x82;break;
		case RESERVED: 			tagByte = (byte)0x92;break;
		case SPEEX: 			tagByte = (byte)0xB2;break;
		case MP3_8: 			tagByte = (byte)0xE2;break;
		case DEVICE_SPECIFIC:	tagByte = (byte)0xF2;break;

		case AAC: 				tagByte = (byte)0xA2;
			setSize(data.remaining() + 2 + 15); // aacの場合はサイズがちょっとかわるので、上書きしておく。
			break;

//		case 12: // 不明
//		case 13: // 未定義
		default:
			throw new RuntimeException("判定不能なコーデック");
		}
		// サンプリングレート
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
		// サウンドチャンネル指定
		if(channels == 2) {
			tagByte |= 1;
		}*/
		// データの作成
		ByteBuffer buffer = ByteBuffer.allocate(getSize());
		// header
		buffer.put(getHeaderBuffer((byte)0x08));
		// tag
//		buffer.put(tagByte);
		buffer.put(getTagByte());
		// aacの場合はmshの判定処理が必要
		if(codec == CodecType.AAC) {
			if(isMediaSequenceHeader) {
				buffer.put((byte)0x00);
			}
			else {
				buffer.put((byte)0x01);
			}
		}
		// 実データ
		buffer.put(data.duplicate());
		// 終端データ
		buffer.put(getTailBuffer());
		// 読み込みモードに変更
		buffer.flip();
		return buffer;
	}
	/**
	 * mediaSequenceHeaderかどうか参照
	 * @return
	 */
	public boolean isMediaSequenceHeader() {
		return isMediaSequenceHeader;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "audioTag:" + getTimestamp() + " codec:" + getCodec();
	}
	/**
	 * 同じタグを作成して応答します
	 */
	public AudioTag clone() {
		AudioTag aTag = new AudioTag(0, getInitSize(), getTimestamp());
		aTag.channels = channels;
		aTag.codec = codec;
		aTag.data = data;
		aTag.isMediaSequenceHeader = isMediaSequenceHeader;
		aTag.sampleRate = sampleRate;
		return aTag;
	}
}
