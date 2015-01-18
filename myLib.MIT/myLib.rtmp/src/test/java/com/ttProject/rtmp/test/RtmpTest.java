/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.rtmp.test;

import org.apache.log4j.Logger;

import com.ttProject.container.flv.amf.Amf0Object;
import com.ttProject.rtmp.IDataListener;
import com.ttProject.rtmp.INetStatusEventListener;
import com.ttProject.rtmp.NetConnection;
import com.ttProject.rtmp.NetStream;
import com.ttProject.rtmp.message.IRtmpMessage;

/**
 * test for rtmp.
 * @author taktod
 */
public class RtmpTest {
	private Logger logger = Logger.getLogger(RtmpTest.class);
//	@Test
	public void test() throws Exception {
		logger.info("start test.");
		final NetConnection nc = new NetConnection();
		nc.setListener(new INetStatusEventListener() {
			@Override
			public void onStatusEvent(Amf0Object<String, Object> obj) {
				logger.info(obj);
				logger.info(obj.get("code"));
				if(obj.get("code").equals("NetConnection.Connect.Success")) {
					logger.info("connect ok, next is to make netStream.");
					NetStream ns = new NetStream(nc); // このタイミングでコネクトしてコネクトがうまくいったらstream.playを実施する
					ns.setListener(new INetStatusEventListener() {
						@Override
						public void onStatusEvent(Amf0Object<String, Object> obj) {
							// netStreamEvent.
							logger.info(obj.get("code"));
						}
					});
					ns.setDataListener(new IDataListener() {
						@Override
						public void receive(IRtmpMessage message) {
							// receive the data, do something.
							logger.info("acquire media message." + message.getClass().getSimpleName());
						}
					});
					ns.play("test");
				}
			}
		});
		nc.connect("rtmp://localhost/live");
		nc.closeForWait();
	}
}
