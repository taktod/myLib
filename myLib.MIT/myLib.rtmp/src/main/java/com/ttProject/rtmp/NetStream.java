/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.rtmp;

import com.ttProject.container.flv.amf.Amf0Object;
import com.ttProject.rtmp.message.IRtmpMessage;
import com.ttProject.rtmp.netty.NetStreamHandler;

/**
 * NetStream
 * @author taktod
 * only play now.
 */
public class NetStream {
	private String streamName;
	@SuppressWarnings("unused")
	private NetStreamHandler handler;
	private INetStatusEventListener listener = null;
	private IDataListener dataListener = null;
	private Type type = Type.Undefined;
	public enum Type {
		Play,
		Publish,
		Undefined
	};
	public NetStream(NetConnection conn) {
		handler = new NetStreamHandler(conn, this);
	}
	public void play(String name) {
		streamName = name;
		type = Type.Play;
	}
	public Type getType() {
		return type;
	}
	public String getStreamName() {
		return streamName;
	}
	public void setListener(INetStatusEventListener listener) {
		this.listener = listener;
	}
	public void setDataListener(IDataListener dataListener) {
		this.dataListener = dataListener;
	}
	public void onStatusEvent(Amf0Object<String, Object> obj) {
		if(listener != null) {
			listener.onStatusEvent(obj);
		}
	}
	public void onMediaReceived(IRtmpMessage message) {
		if(dataListener != null) {
			dataListener.receive(message);
		}
	}
}
