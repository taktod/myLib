/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.rtmp.command;

/**
 * CommandType
 * @author taktod
 */
public enum CommandType {
	Connect("connect"),
	CreateStream("createStream"),
	CloseStream("closeStream"),
	Result("_result"),
	ReceiveAudio("receiveAudio"),
	ReceiveVideo("receiveVideo"),
	Play("play"),
	OnStatus("onStatus"),
	Undefined("");
	private final String value;
	private CommandType(String value) {
		this.value = value;
	}
	public String strValue() {
		return value;
	}
	public static CommandType getValue(String data) {
		for(CommandType t : values()) {
			if(t.strValue().equalsIgnoreCase(data)) {
				return t;
			}
		}
		return CommandType.Undefined;
	}
}
