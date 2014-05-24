/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.convert.ffmpeg.test;

import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ttProject.convert.IConvertListener;
import com.ttProject.convert.ffmpeg.FfmpegConvertManager;
import com.ttProject.convert.ffmpeg.ProcessHandler;
import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IReadChannel;

/**
 * 動作テスト用
 * @author taktod
 */
@SuppressWarnings("unused")
public class ConvertTest {
	private Logger logger = Logger.getLogger(ConvertTest.class);
//	@Test
	public void test() throws Exception {
		// データ元
		IReadChannel fc1 = FileReadChannel.openFileReadChannel("http://49.212.39.17/mario.mp4");
		// 保存先
		final FileOutputStream fos = new FileOutputStream("output.flv");
		// 変換マネージャー
		FfmpegConvertManager manager = new FfmpegConvertManager();
		// 変換動作プロセス取得
		ProcessHandler handler = manager.getProcessHandler("test");
		// 変換出力データの処理
		handler.addListener(new IConvertListener() {
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
		});
		// 処理させる動作コマンド
		handler.setCommand("~/bin/bin/avconv -i - -acodec copy -vcodec copy -f flv -");
		// 処理開始
		manager.start();
		ByteBuffer buf;
		while(true) {
			// ソースファイルからデータ読み込み
			buf = ByteBuffer.allocate(65536);
			fc1.read(buf);
			buf.flip();
			if(buf.remaining() != 0) {
				// データがある場合は変換プログラムにデータを送る
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
