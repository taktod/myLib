/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.convertprocess.process;

import java.nio.channels.Channels;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.ttProject.container.flv.FlvHeaderTag;
import com.ttProject.container.flv.FlvTagWriter;
import com.ttProject.convertprocess.frame.IShareFrameListener;
import com.ttProject.convertprocess.server.ProcessClient;
import com.ttProject.frame.IFrame;
import com.ttProject.frame.IVideoFrame;

/**
 * flvの映像の部分のみ取り出したデータとして、ffmpegにデータを渡すentry
 * @author taktod
 */
public class FlvVideoOutputEntry implements IShareFrameListener {
	/** ロガー */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(FlvVideoOutputEntry.class);
	/** 動作クライアント */
	private ProcessClient client = null;
	/** flvの出力モジュール */
	private FlvTagWriter writer = null;
	private FlvTagWriter writer2 = null;
	/** 標準出力がnettyから紐づいたthreadで出力するようにしておくと、フリーズする問題があったので、executorで吐かせます */
	private ExecutorService exec = Executors.newCachedThreadPool();
	private LinkedBlockingQueue<IFrame> frameQueue = new LinkedBlockingQueue<IFrame>();
	/**
	 * エントリーポイント
	 * @param args
	 */
	public static void main(String args[]) {
		Logger.getRootLogger().setLevel(Level.OFF);
		if(args == null || args.length != 1) {
			System.err.println("引数の数がおかしいです。");
			System.exit(-1);
			return;
		}
		int port = 0;
		try {
			port = Integer.parseInt(args[0]);
		}
		catch(Exception e) {
			System.err.println("入力ポート番号の数値解釈できませんでした。");
			System.exit(-1);
			return;
		}
		FlvVideoOutputEntry entry = new FlvVideoOutputEntry();
		entry.start(port);
	}
	/**
	 * コンストラクタ
	 */
	public FlvVideoOutputEntry() {
		client = new ProcessClient(this);
		// 通常のflvの出力としてデータを出したい。
		try {
			writer = new FlvTagWriter(Channels.newChannel(System.out));
//			new File("videoOnly.flv").delete();
			writer2 = new FlvTagWriter("videoOnly.flv");
			FlvHeaderTag headerTag = new FlvHeaderTag();
			headerTag.setAudioFlag(false);
			headerTag.setVideoFlag(true);
			writer.addContainer(headerTag);
			writer2.addContainer(headerTag);
			Runtime.getRuntime().addShutdownHook(new Thread(){
				@Override
				public void run() {
					try {
						exec.shutdownNow();
						if(writer != null) {
							writer.prepareTailer();
							writer2.prepareTailer();
						}
					}
					catch(Exception e) {
					}
				}
			});
		}
		catch(Exception e) {
			writer = null;
		}
	}
	/**
	 * クライアントアクセス開始
	 * @param port
	 */
	public void start(int port) {
		// この方法だと、フレームの書き込みタイミングでデータが前後する可能性があるっぽいです。
		exec.execute(new Runnable() {
			@Override
			public void run() {
				try {
					while(true) {
						IFrame frame = frameQueue.take();
//						writer.addFrame(9, frame);
						writer2.addFrame(9, frame);
					}
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
		// 動作開始
		client.connect("localhost", port);
		client.waitForClose(); // 終わるまで待機しておく
	}
	private boolean start = false;
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void pushFrame(final IFrame frame, final int id) {
		if(writer != null && frame instanceof IVideoFrame) {
			if(!start) {
				IVideoFrame vFrame = (IVideoFrame)frame;
				if(vFrame.isKeyFrame()) {
					start = true;
				}
				else {
					return;
				}
			}
			frameQueue.add(frame);
		}
	}
}
