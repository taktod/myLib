/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.rtmp.message;

/**
 * MessageType
 * @author taktod
 * define the message object type for RtmpMessage.
 */
public enum MessageType {
	SET_CHUNK_SIZE(0x01),
	ABORT_MESSAGE(0x02),
	ACKNOWLEDGEMENT(0x03),
	USER_CONTROL_MESSAGE(0x04),
	WINDOW_ACKNOWLEDGEMENT_SIZE(0x05),
	SET_PEER_BANDWIDTH(0x06),
	// Unknown(0x07),
	AUDIO_MESSAGE(0x08),
	VIDEO_MESSAGE(0x09),
	// Unknown(0x0A - 0x0E),
	AMF3_DATA_MESSAGE(0x0F),
	AMF3_SHARED_OBJECT_MESSAGE(0x10),
	AMF3_COMMAND(0x11),
	AMF0_DATA_MESSAGE(0x12),
	AMF0_SHARED_OBJECT_MESSAGE(0x13),
	AMF0_COMMAND(0x14),
	// Unknown(0x15),
	AGGREGATE_MESSAGE(0x16);
	
	private final int value;
	private MessageType(final int value) {
		this.value = value;
	}
	public int intValue() {
		return value;
	}
	public static int getDefaultCsId(MessageType type) {
		switch(type) {
		case SET_CHUNK_SIZE:
		case ABORT_MESSAGE:
		case ACKNOWLEDGEMENT:
		case USER_CONTROL_MESSAGE:
		case WINDOW_ACKNOWLEDGEMENT_SIZE:
		case SET_PEER_BANDWIDTH:
			return 2;
		case AMF3_COMMAND:
		case AMF0_COMMAND:
			return 3;
		case AMF0_DATA_MESSAGE: // これ4じゃね？
			return 4;
		case AUDIO_MESSAGE:
		case VIDEO_MESSAGE:
		case AMF3_DATA_MESSAGE:
		case AMF3_SHARED_OBJECT_MESSAGE:
		case AMF0_SHARED_OBJECT_MESSAGE:
		case AGGREGATE_MESSAGE:
		default:
			return 5;
		}
	}
	public static MessageType getType(int value) {
		for(MessageType t : values()) {
			if(t.intValue() == value) {
				return t;
			}
		}
		throw new RuntimeException("out of range.");
	}
}
