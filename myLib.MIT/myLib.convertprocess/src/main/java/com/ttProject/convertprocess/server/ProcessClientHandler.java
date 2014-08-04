/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.convertprocess.server;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipelineCoverage;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

import com.ttProject.convertprocess.frame.ShareFrameData;
import com.ttProject.util.BufferUtil;

/**
 * clientでのデータの受け取りhandler
 * @author taktod
 */
@ChannelPipelineCoverage("one")
public class ProcessClientHandler extends SimpleChannelUpstreamHandler {
	/** ロガー */
	private Logger logger = Logger.getLogger(ProcessClientHandler.class);
	/** データサイズ */
	private int size = -1;
	/** やり取り中のデータbuffer */
	private ByteBuffer buffer = null;
	/**
	 * メッセージをうけとった場合の処理
	 */
	@Override
	public synchronized void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception {
		buffer = BufferUtil.connect(buffer, ((ChannelBuffer)e.getMessage()).toByteBuffer());
		while(buffer.remaining() > 0) {
			if(size == -1) {
				if(buffer.remaining() < 4) { // サイズデータを参照するのに必要なデータがない
					return;
				}
				size = buffer.getInt();
			}
			if(buffer.remaining() < size) { // 共有フレームデータを参照するのに必要なデータがない
				return;
			}
			if(size < 0) { // データがおかしい
				return;
			}
			ByteBuffer data = ByteBuffer.allocate(size);
			byte[] tmp = new byte[size];
			buffer.get(tmp);
			data.put(tmp);
			data.flip();
			try {
				processFrame(data);
			}
			catch(Exception ex) {
				logger.error("フレーム複製時に例外が発生しました。", ex);
			}
			size = -1;
		}
	}
	/**
	 * フレームの処理を進める
	 * @param data
	 * @throws Exception
	 */
	private void processFrame(ByteBuffer data) throws Exception {
		ShareFrameData shareFrameData = new ShareFrameData(data);
		logger.info(shareFrameData.getCodecType());
	}
}
