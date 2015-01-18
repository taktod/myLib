/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.rtmp.command;

import com.ttProject.container.flv.amf.Amf0Object;
import com.ttProject.rtmp.message.type.Amf0Command;

/**
 * Amf0
 * @author taktod
 * Helper for Amf0Command.
 */
public class Amf0 {
	/**
	 * connect
	 * @param transactionId
	 * @return
	 */
	public static Amf0Command connect(int transactionId, String tcUrl) {
		Amf0Command connect = new Amf0Command();
		connect.setCommandType(CommandType.Connect);
		connect.setTransactionId(transactionId);
		Amf0Object<String, Object> connectParams = new Amf0Object<String, Object>();
		connectParams.put("app", "live");
		connectParams.put("flashVer", "WIN 15,0,0,223");
		connectParams.put("tcUrl", tcUrl);
		connectParams.put("fpad", false);
		connectParams.put("audioCodecs", 0xFE7);
		connectParams.put("videoCodecs", 0xFC);
		connectParams.put("objectEncoding", 0);
		connectParams.put("capabilities", 15);
		connectParams.put("videoFunction", 1);
		connect.setObject(connectParams);
		return connect;
	}
	/**
	 * createStream
	 * @param transactionId
	 * @return
	 */
	public static Amf0Command createStream(int transactionId) {
		Amf0Command createStream = new Amf0Command();
		createStream.setTransactionId(transactionId);
		createStream.setCommandType(CommandType.CreateStream);
		return createStream;
	}
	/**
	 * receiveAudio
	 * @return
	 */
	public static Amf0Command receiveAudio(boolean flag) {
		Amf0Command receiveAudio = new Amf0Command();
		receiveAudio.setCommandType(CommandType.ReceiveAudio);
		receiveAudio.setExtra(flag);
		return receiveAudio;
	}
	/**
	 * receiveVideo
	 * @return
	 */
	public static Amf0Command receiveVideo(boolean flag) {
		Amf0Command receiveVideo = new Amf0Command();
		receiveVideo.setCommandType(CommandType.ReceiveVideo);
		receiveVideo.setExtra(flag);
		return receiveVideo;
	}
	/**
	 * play
	 * @param name
	 * @param streamId
	 * @return
	 */
	public static Amf0Command play(String name, int streamId) {
		Amf0Command play = new Amf0Command();
		play.setCommandType(CommandType.Play);
		play.setTransactionId(0);
		play.setExtra(name);
		play.getHeader().setCsId(8);
		play.getHeader().setStreamId(streamId);
		return play;
	}
	/**
	 * closeStream(not tested yet.)
	 * @param streamId
	 * @return
	 */
	public static Amf0Command closeStream(int streamId) {
		Amf0Command closeStream = new Amf0Command();
		closeStream.setTransactionId(0);
		closeStream.setCommandType(CommandType.CloseStream);
		closeStream.getHeader().setCsId(8);
		closeStream.getHeader().setStreamId(streamId);
		return closeStream;
	}
}
