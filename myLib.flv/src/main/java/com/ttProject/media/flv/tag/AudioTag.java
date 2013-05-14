package com.ttProject.media.flv.tag;

import com.ttProject.media.flv.CodecType;
import com.ttProject.media.flv.Tag;
import com.ttProject.nio.channels.IFileReadChannel;

/**
 * audioデータ
 * @author taktod
 */
public class AudioTag extends Tag {
	/** コーデック */
	private CodecType audioCodec;
	/** aacのmediaSequenceHeaderであるかどうか */
	private boolean isMediaSequenceHeader = false;
	/**
	 * コンストラクタ
	 * @param size
	 * @param position
	 * @param timestamp
	 * @param codecByte
	 * @param mshFlg
	 */
	public AudioTag(final int size, final int position, final int timestamp, byte codecByte, byte mshFlg) {
		super(size, position, timestamp);
		audioCodec = CodecType.getAudioCodecType(codecByte);
		if(audioCodec == CodecType.AAC) {
			isMediaSequenceHeader = (mshFlg == 0x00);
		}
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void analyze(IFileReadChannel ch) throws Exception {
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
		StringBuilder data = new StringBuilder();
		data.append("audio:").append(Integer.toHexString(getTimestamp()));
		data.append(" ").append(audioCodec);
		if(audioCodec == CodecType.AAC && isMediaSequenceHeader) {
			data.append("*");
		}
		return data.toString();
	}
}
