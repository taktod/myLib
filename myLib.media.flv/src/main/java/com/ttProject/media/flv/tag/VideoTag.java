package com.ttProject.media.flv.tag;

import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

import com.ttProject.media.flv.CodecType;
import com.ttProject.media.flv.Tag;
import com.ttProject.nio.channels.IFileReadChannel;
import com.ttProject.util.BufferUtil;

/**
 * videoデータ
 * @author taktod
 */
public class VideoTag extends Tag {
	/** コーデック */
	private CodecType codec;
	/** フレーム情報 */
	private FrameType frame;
	/**
	 * フレームタイプ定義
	 */
	private enum FrameType {
		Key,
		Inner,
		Disposable
	}
	/** avc(h.264)のmediaSequenceHeaderであるかどうか */
	private boolean isMediaSequenceHeader = false;
	/** データ本体 */
	private ByteBuffer data;
	/**
	 * コンストラクタ
	 */
	public VideoTag() {
		super();
	}
	/**
	 * コンストラクタ
	 * @param size
	 * @param position
	 * @param timestamp
	 */
	public VideoTag(final int size, final int position, final int timestamp) {
		super(size, position, timestamp);
	}
	/**
	 * byteデータからコーデックとframeタイプを解析する
	 * @param tagByte
	 * @return true: mediaSequenceHeaderの判定データが必要 false:いらない
	 */
	public boolean analyzeTagByte(byte tagByte) {
		switch(tagByte & 0xF0) {
		case 0x10:
			frame = FrameType.Key;
			break;
		default:
		case 0x20:
			frame = FrameType.Inner;
			break;
		case 0x30:
			frame = FrameType.Disposable;
			break;
		}
		codec = CodecType.getVideoCodecType(tagByte);
		// h.264である場合はmshが必要
		return (codec == CodecType.AVC);
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
	 * フレームタイプを設定する
	 * @param keyFrame
	 */
	public void setFrameType(boolean keyFrame) {
		if(keyFrame) {
			frame = FrameType.Key;
		}
		else {
			frame = FrameType.Inner;
		}
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
	 * @throws Exception
	 */
	public void setData(IFileReadChannel source, int size) throws Exception {
		if(codec == CodecType.AVC) {
			// h.264の場合は、頭に00 00 00をいれてsourceのデータコピーが必要になる。
			data = ByteBuffer.allocate(size + 3);
			data.put((byte)0x00);
			data.put((byte)0x00);
			data.put((byte)0x00);
			data.put(BufferUtil.safeRead(source, size));
			data.flip();
		}
		else {
			data = BufferUtil.safeRead(source, size);
		}
	}
	/**
	 * データを登録しておく
	 * @param buffer
	 */
	public void setData(ByteBuffer buffer) {
		data = buffer.duplicate();
	}
	/**
	 * キーフレームであるか応答する。
	 * @return
	 */
	public boolean isKeyFrame() {
		return frame == FrameType.Key;
	}
	/**
	 * disposableInnerFrameであるか応答する。
	 * @return
	 */
	public boolean isDisposableInner() {
		return frame == FrameType.Disposable;
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
		if(codec == CodecType.AVC) {
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
		// tagの作成
		byte tagByte = 0x00;
		// デフォルトサイズの更新
		setSize(data.remaining() + 1);
		switch(codec) {
		case JPEG:			tagByte = 0x01;	break;
		case H263:			tagByte = 0x02;	break;
		case SCREEN:		tagByte = 0x03;	break;
		case ON2VP6:		tagByte = 0x04;	break;
		case ON2VP6_ALPHA:	tagByte = 0x05;	break;
		case SCREEN_V2:		tagByte = 0x06;	break;
		case AVC:			tagByte = 0x07;
			setSize(data.remaining() + 2); // avcの場合はサイズがかわるの修正しておく。
			break;
		default:
			throw new Exception("定義されていないコーデックです。");
		}
		switch(frame) {
		case Key:			tagByte |= 0x10;break;
		case Inner:			tagByte |= 0x20;break;
		case Disposable:	tagByte |= 0x30;break;
		default:
			throw new Exception("定義されていないフレームです。");
		}
		ByteBuffer buffer = ByteBuffer.allocate(getRealSize());
		// header
		buffer.put(getHeaderBuffer((byte)0x09));
		// tag
		buffer.put(tagByte);
		// avcの場合はmsh判定が必要
		if(codec == CodecType.AVC) {
			if(isMediaSequenceHeader) {
				buffer.put((byte)0x00);
			}
			else {
				buffer.put((byte)0x01);
			}
		}
		// 実データ
		buffer.put(data);
		// 終端処理
		buffer.put(getTailBuffer());
		// 読み込みモードにする。
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
		return "videoTag:" + getTimestamp();
	}
}
