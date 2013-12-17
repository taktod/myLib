package com.ttProject.frame.vp6;

import java.nio.ByteBuffer;

import com.ttProject.frame.VideoFrame;
import com.ttProject.frame.vp6.type.IntraFrame;
import com.ttProject.unit.extra.BitConnector;
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit6;

/**
 * on2Vp6のコーデックの映像の内容を解析します。
 * @see http://wiki.multimedia.cx/index.php?title=On2_VP6
 * 00 78 46 0F 14 0F 14 3F 6E E8 CB 01 8D C9 89 26 9E AD 53 6F 33 FD DD F2 BF AB F6 ED FB 1C
 * flvにはいっているvp6のデータの先頭は、終端にくる必要があるらしい。
 * よって解析データは78から・・・となります。
 * 
 * @see http://hkpr.info/flash/swf/index.php?%E3%83%93%E3%83%87%E3%82%AA%2FOn2%20Truemotion%20VP6%20%E3%83%93%E3%83%83%E3%83%88%E3%82%B9%E3%83%88%E3%83%AA%E3%83%BC%E3%83%A0%E3%83%95%E3%82%A9%E3%83%BC%E3%83%9E%E3%83%83%E3%83%88
 * on2vp6alphaも
 * はじめの00の部分はalignのずれ設定みたいですね。
 * データは捨てた方がよさそう・・・
 * 
 * @author taktod
 */
public abstract class Vp6Frame extends VideoFrame {
	private final Bit1 frameMode;
	private final Bit6 qp;
	private final Bit1 marker;
	private IntraFrame keyFrame = null;
	public Vp6Frame(Bit1 frameMode, Bit6 qp, Bit1 marker) {
		this.frameMode = frameMode;
		this.qp = qp;
		this.marker = marker;
	}
	public void setKeyFrame(IntraFrame keyFrame) {
		this.keyFrame = keyFrame;
		super.setWidth(keyFrame.getWidth());
		super.setHeight(keyFrame.getHeight());
	}
	protected Bit1 getMarker() {
		return marker;
	}
	protected IntraFrame getKeyFrame() {
		return keyFrame;
	}
	protected ByteBuffer getHeaderBuffer() {
		BitConnector connector = new BitConnector();
		return connector.connect(frameMode, qp, marker);
	}
}
