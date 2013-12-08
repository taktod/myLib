package com.ttProject.frame.vp6;

import com.ttProject.frame.VideoFrame;
import com.ttProject.frame.vp6.type.IntraFrame;
import com.ttProject.unit.extra.Bit1;
import com.ttProject.unit.extra.Bit6;

/**
 * on2Vp6のコーデックの映像の内容を解析します。
 * @see http://wiki.multimedia.cx/index.php?title=On2_VP6
 * 00 78 46 0F 14 0F 14 3F 6E E8 CB 01 8D C9 89 26 9E AD 53 6F 33 FD DD F2 BF AB F6 ED FB 1C
 * flvにはいっているvp6のデータの先頭は、終端にくる必要があるらしい。
 * よって解析データは78から・・・となります。
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
}
