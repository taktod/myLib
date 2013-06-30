package com.ttProject.media.flv.tag;

import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

import com.ttProject.media.flv.CodecType;
import com.ttProject.media.flv.Tag;
import com.ttProject.nio.channels.IFileReadChannel;
import com.ttProject.util.BufferUtil;

/**
 * audioデータ
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
	 * 音声チャンネル数を設定
	 * @param channels
	 */
	public void setChannels(byte channels) {
		this.channels = channels;
	}
	/**
	 * mshであるか設定する
	 * @param flg
	 */
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
		// ファイルからの解析はあとでつくっておく。
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
		target.write(getBuffer());
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ByteBuffer getBuffer() throws Exception {
		data.position(0);
		// tagを作成します。
		byte tagByte = 0x00;
		// デフォルトサイズの更新
		setSize(data.remaining() + 1);
		// codec判定
		switch(codec) {
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
			setSize(data.remaining() + 2); // aacの場合はサイズがちょっとかわるので、上書きしておく。
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
		// データの作成
		ByteBuffer buffer = ByteBuffer.allocate(getRealSize());
		// header
		buffer.put(getHeaderBuffer((byte)0x08));
		// tag
		buffer.put(tagByte);
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
		buffer.put(data);
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
		return "audioTag:" + getTimestamp();
	}
}
