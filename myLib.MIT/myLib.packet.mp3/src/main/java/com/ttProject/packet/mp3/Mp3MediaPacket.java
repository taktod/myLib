/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.packet.mp3;

/**
 * mp3の実体パケット
 * @author taktod
 */
public class Mp3MediaPacket extends Mp3Packet {
	/**
	 * コンストラクタ
	 * @param manager
	 */
	public Mp3MediaPacket(Mp3PacketManager manager) {
		super(manager);
	}
	/**
	 * headerパケットであるか？
	 */
	@Override
	public boolean isHeader() {
		return false;
	}
}
