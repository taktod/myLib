package com.ttProject.media.mpegts.packet;

import com.ttProject.media.mpegts.Packet;

public class Media extends Packet {
	private boolean pcrFlg = false;
	public Media() {
		super(0);
	}
}
