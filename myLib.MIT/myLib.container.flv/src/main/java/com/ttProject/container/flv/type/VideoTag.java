package com.ttProject.container.flv.type;

import com.ttProject.container.flv.FlvTag;
import com.ttProject.unit.extra.Bit4;
import com.ttProject.unit.extra.Bit8;
import com.ttProject.unit.extra.BitN.Bit24;

/**
 * 映像用のtag
 * @author taktod
 */
public class VideoTag extends FlvTag {
	private Bit4 frameType;
	private Bit4 codecId;
	private Bit8 packetType; // avcのみ
	private Bit24 dts; // avcのみ
}
