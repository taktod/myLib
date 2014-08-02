/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.convert.ffmpeg.test;

import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ttProject.container.IContainer;
import com.ttProject.container.flv.FlvTagReader;
import com.ttProject.convert.ffmpeg.FfmpegConvertManager;
import com.ttProject.convert.ffmpeg.ProcessHandler;
import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IReadChannel;

/**
 * 動作テスト用
 * @author taktod
 */
public class ConvertTest {
	private Logger logger = Logger.getLogger(ConvertTest.class);
	@Test
	public void test() throws Exception {
		// データ元
		IReadChannel fc1 = FileReadChannel.openFileReadChannel("http://49.212.39.17/mario.mp4");
		// 保存先
		final FileOutputStream fos = new FileOutputStream("output.flv");
		// 変換マネージャー
		FfmpegConvertManager manager = new FfmpegConvertManager();
		// 変換動作プロセス取得
		ProcessHandler handler = manager.getProcessHandler("test");
		// processHandlerを複数つくって、複数取り出す形にしておけばよい
		// 変換出力データの処理
/*		handler.addListener(new IConvertListener() {
			@Override
			public void receiveData(ByteBuffer buffer) {
				try {
					// 応答をうけとったらファイルに書き出す。
					logger.info("応答をうけとりました。");
					fos.getChannel().write(buffer);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		});*/
		// 処理させる動作コマンド
		handler.setCommand("/usr/local/bin/avconv -i - -acodec libmp3lame -vcodec flv1 -f flv -");
		Map<String, String> map = new HashMap<String, String>();
		map.put("DYLD_LIBRARY_PATH", "/usr/local/lib");
		handler.setEnvExtra(map);
		// 処理開始
		manager.start();
		// スタート後にIReadChannelを取得することができるが、別threadとして動作させないとだめ
		ExecutorService executorService = Executors.newCachedThreadPool();
		final IReadChannel resultChannel = handler.getReadChannel();
		executorService.execute(new Runnable() {
			@Override
			public void run() {
				// ここで処理を実行します。
				try {
					FlvTagReader reader = new FlvTagReader();
					IContainer container = null;
					while((container = reader.read(resultChannel)) != null) {
						logger.info(container);
					}
				}
				catch(Exception e) {
					logger.error("例外が発生しておわってしまいました。", e);
				}
				logger.info("変換の出力の取得処理がおわりました。");
			}
		});
		ByteBuffer buf;
		while(true) {
			// ソースファイルからデータ読み込み
			buf = ByteBuffer.allocate(65536);
			fc1.read(buf);
			buf.flip();
			if(buf.remaining() != 0) {
				// データがある場合は変換プログラムにデータを送る
				logger.info("流し込む");
				manager.applyData(buf);
			}
			// 読み込み元ファイルを読み切ったらおわり。
			if(fc1.position() == fc1.size()) {
				break;
			}
		}
		// ちょっと待っとく。
		Thread.sleep(2000);
		// 後処理
		fc1.close();
		fos.close();
	}
}
