package com.ttProject.frame.vorbis;

import com.ttProject.frame.AudioFrame;
import com.ttProject.frame.vorbis.type.IdentificationHeaderFrame;

/**
 * vorbisのframe
 * @author taktod
 * vorbisのframeもspeexと同様
 * header部
 * コメント部
 * 情報部
 * データ部にわかれるっぽい。
 * 
 * で、データ部だけ、xuggleのframeとしてほしいところ。
 */
public abstract class VorbisFrame extends AudioFrame {
	/** データ参照用のIdentificationHeaderFrame */
	private IdentificationHeaderFrame identificationHeaderFrame = null;
	/**
	 * identificationHeaderFrame(情報を保持している)を設定
	 * @param headerFrame
	 */
	public void setIdentificationHeaderFrame(IdentificationHeaderFrame headerFrame) {
		this.identificationHeaderFrame = headerFrame;
	}
	/**
	 * identificationHeaderFrameを参照
	 * @return
	 */
	protected IdentificationHeaderFrame getHeaderFrame() {
		return identificationHeaderFrame;
	}
}
