/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.convertprocess.server;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipelineCoverage;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

import com.ttProject.convertprocess.frame.AnalyzerChecker;
import com.ttProject.convertprocess.frame.IShareFrameListener;
import com.ttProject.convertprocess.frame.ShareFrameData;
import com.ttProject.frame.AudioAnalyzer;
import com.ttProject.frame.Frame;
import com.ttProject.frame.IAnalyzer;
import com.ttProject.frame.IFrame;
import com.ttProject.frame.NullFrame;
import com.ttProject.frame.VideoAnalyzer;
import com.ttProject.frame.VideoFrame;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;
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
	/** analyzerのmap */
	private Map<Integer, IAnalyzer> analyzerMap = new HashMap<Integer, IAnalyzer>();
	/** analyzerの確認をするモジュール */
	private AnalyzerChecker analyzerChecker = new AnalyzerChecker();
	/** フレームを取得したときのlistener */
	private final IShareFrameListener listener;
	/**
	 * コンストラクタ
	 * @param listener
	 */
	public ProcessClientHandler(IShareFrameListener listener) {
		this.listener = listener;
	}
	@Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e)
			throws Exception {
		ChannelBuffer buf = ChannelBuffers.buffer("hello".length());
		buf.writeBytes("hello".getBytes());
		e.getChannel().write(buf);
	}
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
			throws Exception {
		e.getCause().printStackTrace();
	}
	/**
	 * メッセージをうけとった場合の処理
	 */
	@Override
	public synchronized void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception {
//		System.err.println("データをうけとりました。");
		ByteBuffer cbuf = ((ChannelBuffer)e.getMessage()).toByteBuffer();
//		System.err.println("確認:" + cbuf.limit() + " " + cbuf.capacity() + " " + cbuf.remaining());
		buffer = BufferUtil.connect(buffer, cbuf);
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
				ex.printStackTrace();
//				logger.error("フレーム複製時に例外が発生しました。", ex);
			}
			size = -1;
		}
//		System.err.println("messageReceivedおわり");
	}
	/**
	 * フレームの処理を進める
	 * @param data
	 * @throws Exception
	 */
	private void processFrame(ByteBuffer data) throws Exception {
		ShareFrameData shareFrameData = new ShareFrameData(data);
		IAnalyzer analyzer = analyzerMap.get(shareFrameData.getTrackId());
		if(analyzer == null) {
			analyzer = analyzerChecker.checkAnalyzer(shareFrameData.getCodecType());
			if(analyzer instanceof AudioAnalyzer) {
				shareFrameData.setupFrameSelector(((AudioAnalyzer) analyzer).getSelector());
			}
			else if(analyzer instanceof VideoAnalyzer){
				shareFrameData.setupFrameSelector(((VideoAnalyzer) analyzer).getSelector());
			}
			else {
				throw new Exception("Analyzerが不明でした。");
			}
			analyzerMap.put(shareFrameData.getTrackId(), analyzer);
		}
//		System.err.println("shareFrameData取得完了:" + shareFrameData.getFrameData().remaining());
		// この部分でframeの値をとれるだけとらないとだめ。
		IFrame frame = null;
		IReadChannel channel = new ByteReadChannel(shareFrameData.getFrameData());
		while((frame = analyzer.analyze(channel)) != null) {
			completeFrame(frame, shareFrameData);
		}
		frame = analyzer.getRemainFrame();
		if(frame != null && !(frame instanceof NullFrame)) {
			completeFrame(frame, shareFrameData);
		}
//		System.err.println("フレーム処理完了");
	}
	/**
	 * 出来上がったデータを整形する
	 * @param frame
	 * @param shareFrameData
	 * @throws Exception
	 */
	private void completeFrame(IFrame frame, ShareFrameData shareFrameData) throws Exception {
		Frame f = (Frame)frame;
		f.setTimebase(shareFrameData.getTimebase());
		f.setPts(shareFrameData.getPts());
		if(frame instanceof NullFrame) {
			// nullFrameになっている場合は、処理する必要なし
			return;
		}
		if(frame instanceof VideoFrame) {
			// 動画の場合はdtsをいれておく。
			VideoFrame vFrame = (VideoFrame)frame;
			vFrame.setDts(shareFrameData.getDts());
		}
		
		// ここでみつかったデータをlistenerに渡しておく
		listener.pushFrame(frame, shareFrameData.getTrackId());
	}
}
