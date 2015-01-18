/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.rtmp.handshake;

import java.util.Random;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * RtmpHandshake
 * @author taktod
 * to manage rtmpHandshake
 */
public class RtmpHandshake {
	public static final int HANDSHAKE_SIZE = 1536;
	private ByteBuf serverResponseBuf = null;
	private byte[]  peerTime = null;
	private byte[]  serverVersion = null;
	public ByteBuf clientRequest0() {
		ByteBuf out = Unpooled.buffer(1);
		out.writeByte((byte)0x03); // not rtmpe
		return out;
	}
	public ByteBuf clientRequest1() {
		byte[] randomBytes = new byte[HANDSHAKE_SIZE];
		Random random = new Random();
		random.nextBytes(randomBytes);
		ByteBuf out = Unpooled.wrappedBuffer(randomBytes);
		out.setInt(0, (int)(System.currentTimeMillis() / 1000)); // epoc time.
		out.setInt(4, 0); // zeros
		return out;
	}
	public void serverResponse0(ByteBuf in) {
		byte flag = in.getByte(0);
		if(flag != 0x03) {
			throw new RuntimeException("rtmpe is not support.");
		}
	}
	public void serverResponse1(ByteBuf in) {
		peerTime = new byte[4];
		in.getBytes(0, peerTime);
		serverVersion = new byte[4];
		in.getBytes(4, serverVersion);
		serverResponseBuf = in.copy();
	}
	public ByteBuf clientRequest2() {
		return serverResponseBuf;
	}
	public void serverResponse2(ByteBuf in) {
	}
}
