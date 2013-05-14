package com.ttProject.media.flv.tag;

import com.ttProject.media.flv.CodecType;
import com.ttProject.media.flv.Tag;
import com.ttProject.nio.channels.IFileReadChannel;

/**
 * videoデータ
 * @author taktod
 */
public class VideoTag extends Tag {
	/** コーデック */
	private CodecType videoCodec;
	/** avc(h.264)のmediaSequenceHeaderであるかどうか */
	private boolean isMediaSequenceHeader = false;
	/**
	 * コンストラクタ
	 * @param size
	 * @param position
	 * @param timestamp
	 * @param codecTag
	 * @param mshFlg
	 */
	public VideoTag(final int size, final int position, final int timestamp, byte codecTag, byte mshFlg) {
		super(size, position, timestamp);
		videoCodec = CodecType.getVideoCodecType(codecTag);
		if(videoCodec == CodecType.AVC) {
			isMediaSequenceHeader = (mshFlg == 0x00);
		}
	}
	/**
	 * 解析
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
	public String toString() {
		StringBuilder data = new StringBuilder();
		data.append("video:").append(Integer.toHexString(getTimestamp()));
		data.append(" ").append(videoCodec);
		if(videoCodec == CodecType.AVC && isMediaSequenceHeader) {
			data.append("*");
		}
		return data.toString();
	};
}
