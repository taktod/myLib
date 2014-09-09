/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.packet.flv;

import com.ttProject.packet.MediaPacket;

public abstract class FlvPacket extends MediaPacket {
	private final FlvPacketManager manager;
	public FlvPacket(FlvPacketManager manager) {
		this.manager = manager;
	}
	protected FlvPacketManager getManager() {
		return manager;
	}
	/**
	 * ヘッダーのサイズ解析
	 * @param header
	 * @return
	 */
	protected int getSizeFromHeader(byte[] header) {
		return (((header[1] & 0xFF) << 16) + ((header[2] & 0xFF) << 8) + (header[3] & 0xFF));
	}
	/**
	 * ヘッダーの時間解析
	 * @param header
	 * @return
	 */
	protected long getTimeFromHeader(byte[] header) {
		return (((header[4] & 0xFF) << 16) + ((header[5] & 0xFF) << 8) + (header[6] & 0xFF) + ((header[7] & 0xFF) << 24));
	}
}
