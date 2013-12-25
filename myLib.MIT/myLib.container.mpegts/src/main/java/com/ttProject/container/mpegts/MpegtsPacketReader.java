package com.ttProject.container.mpegts;

import com.ttProject.container.Reader;

/**
 * mpegtsPacketを解析します。
 * @author taktod
 */
public class MpegtsPacketReader extends Reader {
	public MpegtsPacketReader() {
		super(new MpegtsPacketSelector());
	}
}
