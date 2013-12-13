package com.ttProject.container.ogg;

import com.ttProject.container.Container;
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit5;
import com.ttProject.unit.extra.bit.Bit8;

/**
 * oggはoggPageというものができているらしい。
 * @author taktod
 */
public abstract class OggPage extends Container {
	private String CapturePattern = "OggS"; // 固定のはず
	private final Bit8 version;
	private final Bit5 zeroFill;
	private final Bit1 logicEndFlag;
	private final Bit1 logicStartFlag;
	private final Bit1 packetContinurousFlag;
	/**
	 * コンストラクタ
	 * @param version
	 * @param zeroFill
	 * @param logicEndFlag
	 * @param logicStartFlag
	 * @param packetContinurousFlag
	 */
	public OggPage(Bit8 version, Bit5 zeroFill,
			Bit1 logicEndFlag, Bit1 logicStartFlag,
			Bit1 packetContinurousFlag) {
		this.version = version;
		this.zeroFill = zeroFill;
		this.logicEndFlag = logicEndFlag;
		this.logicStartFlag = logicStartFlag;
		this.packetContinurousFlag = packetContinurousFlag;
	}
}
