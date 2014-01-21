package com.ttProject.frame.vp8;

import java.nio.ByteBuffer;

import com.ttProject.frame.VideoFrame;
import com.ttProject.frame.vp8.type.KeyFrame;
import com.ttProject.unit.extra.BitConnector;
import com.ttProject.unit.extra.BitN;
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit19;
import com.ttProject.unit.extra.bit.Bit3;
import com.ttProject.unit.extra.bit.Bit8;

/**
 * Vp8のフレームのベース
 * @author taktod
 *
 */
public abstract class Vp8Frame extends VideoFrame {
	private final Bit1  frameType; // 0ならkyeFrame
	private final Bit3  version;
	private final Bit1  showFrame;
	private final Bit19 firstPartSize;
	/** 参照用のキーフレーム */
	private KeyFrame keyFrame = null;
	/**
	 * コンストラクタ
	 * @param frameType
	 * @param version
	 * @param showFrame
	 * @param firstPartSize
	 */
	public Vp8Frame(Bit1 frameType, Bit3 version, Bit1 showFrame, Bit19 firstPartSize) {
		this.frameType     = frameType;
		this.version       = version;
		this.showFrame     = showFrame;
		this.firstPartSize = firstPartSize;
	}
	/**
	 * キーフレーム設定
	 * @param keyFrame
	 */
	public void setKeyFrame(KeyFrame keyFrame) {
		this.keyFrame = keyFrame;
		super.setWidth(keyFrame.getWidth());
		super.setHeight(keyFrame.getHeight());
	}
	/**
	 * キーフレーム参照
	 * @return
	 */
	protected KeyFrame getKeyFrame() {
		return keyFrame;
	}
	protected ByteBuffer getHeaderBuffer() {
		BitConnector connector = new BitConnector();
		Bit3 size_1 = new Bit3();
		Bit8 size_2 = new Bit8();
		Bit8 size_3 = new Bit8();
		BitN sizeBit = new BitN(size_3, size_2, size_1);
		sizeBit.set(firstPartSize.get());
		return connector.connect(size_1, showFrame, version, frameType, size_2, size_3);
	}
}
