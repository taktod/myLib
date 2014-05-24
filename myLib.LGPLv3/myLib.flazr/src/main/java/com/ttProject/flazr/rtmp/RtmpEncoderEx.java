/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under GNU LESSER GENERAL PUBLIC LICENSE Version 3.
 */
package com.ttProject.flazr.rtmp;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipelineCoverage;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelDownstreamHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flazr.rtmp.RtmpHeader;
import com.flazr.rtmp.RtmpMessage;
import com.flazr.rtmp.message.ChunkSize;
import com.flazr.rtmp.message.Control;

/**
 * flazrのrtmpEncoderにちょっとしたバグがあったので修正しました。
 * 同データ判定がpacketサイズのみになっているため、たとえば
 * 音声データ(171byte) 映像データ(171byte)みたいな連なりがある場合に
 * 送信した場合に
 * 音声データ(171byte) 音声データ(171byte)に誤送信されるバグがあるみたいです。
 * @author taktod
 *
 */
@ChannelPipelineCoverage("one")
public class RtmpEncoderEx extends SimpleChannelDownstreamHandler {
	/** 動作ロガー */
	private static final Logger logger = LoggerFactory.getLogger(RtmpEncoderEx.class);
	/** 動作chunkSize */
	private int chunkSize = 128;
	private RtmpHeader[] channelPrevHeaders = new RtmpHeader[RtmpHeader.MAX_CHANNEL_ID];
	private void clearPrevHeaders() {
		logger.debug("clearing prev stream headers");
		channelPrevHeaders = new RtmpHeader[RtmpHeader.MAX_CHANNEL_ID];
	}
	@Override
	public void writeRequested(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception {
		Channels.write(ctx, e.getFuture(), encode((RtmpMessage) e.getMessage()));
	}
	public ChannelBuffer encode(final RtmpMessage message) {
		final ChannelBuffer in = message.encode();
		final RtmpHeader header = message.getHeader();
		if(header.isChunkSize()) {
			final ChunkSize csMessage = (ChunkSize) message;
			logger.debug("encoder new chunk size: {}", csMessage);
			chunkSize = csMessage.getChunkSize();
		}
		else if(header.isControl()) {
			final Control control = (Control)message;
			if(control.getType() == Control.Type.STREAM_BEGIN) {
				clearPrevHeaders();
			}
		}
		final int channelId = header.getChannelId();
		header.setSize(in.readableBytes());
		final RtmpHeader prevHeader = channelPrevHeaders[channelId];
		if(prevHeader != null // first stream message is always large
				&& header.getStreamId() > 0 // all control messages always large
				&& header.getTime() > 0) { // if time is zero. always large
			if(header.getSize() == prevHeader.getSize()
					&& header.getMessageType() == prevHeader.getMessageType()) {
				header.setHeaderType(RtmpHeader.Type.SMALL);
			}
			else {
				header.setHeaderType(RtmpHeader.Type.MEDIUM);
			}
			final int deltaTime = header.getTime() - prevHeader.getTime();
			if(deltaTime < 0) {
				logger.warn("negative time: {}", header);
				header.setDeltaTime(0);
			}
			else {
				header.setDeltaTime(deltaTime);
			}
		}
		else {
			header.setHeaderType(RtmpHeader.Type.LARGE);
		}
		channelPrevHeaders[channelId] = header;
		if(logger.isDebugEnabled()) {
			logger.debug(">> {}", message);
		}
		final ChannelBuffer out = ChannelBuffers.buffer(
				RtmpHeader.MAX_ENCODED_SIZE + header.getSize() + header.getSize() / chunkSize);
		boolean first = true;
		while(in.readable()) {
			final int size = Math.min(chunkSize, in.readableBytes());
			if(first) {
				header.encode(out);
				first = false;
			}
			else {
				out.writeBytes(header.getTinyHeader());
			}
			in.readBytes(out, size);
		}
		return out;
	}
}
