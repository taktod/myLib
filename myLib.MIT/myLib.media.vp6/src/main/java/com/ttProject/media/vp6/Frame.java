package com.ttProject.media.vp6;

import java.nio.ByteBuffer;

import com.ttProject.media.IVideoData;
import com.ttProject.media.Unit;
import com.ttProject.media.extra.Bit1;
import com.ttProject.media.extra.Bit6;

/**
 * on2Vp6のコーデックの映像の内容を解析します。
 * @see http://wiki.multimedia.cx/index.php?title=On2_VP6
 * 00 78 46 0F 14 0F 14 3F 6E E8 CB 01 8D C9 89 26 9E AD 53 6F 33 FD DD F2 BF AB F6 ED FB 1C
 * flvにはいっているvp6のデータの先頭は、終端にくる必要があるらしい。
 * よって解析データは78から・・・となります。
 * @author taktod
 */
public abstract class Frame extends Unit implements IVideoData {
	private Bit1 frameMode;
	private Bit6 qp;
	private Bit1 marker;
	public Frame(Bit1 frameMode, Bit6 qp, Bit1 marker) {
		super(0, 0);
		this.frameMode = frameMode;
		this.qp = qp;
		this.marker = marker;
	}
	@Override
	public long getPts() {
		return 0;
	}

	@Override
	public long getDts() {
		return 0;
	}

	@Override
	public double getTimebase() {
		return 0;
	}

	@Override
	public ByteBuffer getRawData() throws Exception {
		return null;
	}
	protected Bit1 getFrameMode() {
		return frameMode;
	}
	protected Bit6 getQp() {
		return qp;
	}
	protected Bit1 getMarker() {
		return marker;
	}
	
}
