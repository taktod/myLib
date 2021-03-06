/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.packet.mp3;

public class Mp3HeaderPacket extends Mp3Packet {
	/**
	 * コンストラクタ
	 * @param manager
	 */
	public Mp3HeaderPacket(Mp3PacketManager manager) {
		super(manager);
	}
	@Override
	public boolean isHeader() {
		return true;
	}
}
