package com.ttProject.transcode.ffmpeg.worker;

import org.apache.log4j.Logger;

/**
 * データを送り込みます。
 * ffmpegのプロセスが受け入れ完了してからデータを送信する必要がある。
 * @author taktod
 */
public class DataSendWorker implements Runnable {
	/** 動作ロガー */
	private final Logger logger = Logger.getLogger(DataSendWorker.class);
	@Override
	public void run() {

	}
}
