package com.ttProject.transcode.xuggle.test;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.junit.Test;

/**
 * executorserviceを使うかループthreadを使うかまよっているので、ちょっとテストしてみる。
 * @author taktod
 */
public class ThreadTest {
	/** 動作ロガー */
	private Logger logger = Logger.getLogger(ThreadTest.class);
	/**
	 * テスト
	 */
	@Test
	public void test() {
		ExecutorService exec = Executors.newFixedThreadPool(3);
		final ExecutorService single = Executors.newSingleThreadExecutor();
		try {
			for(int i = 0;i < 10;i ++) {
				System.out.println("処理:" + i);
				exec.execute(new Runnable() {
					@Override
					public void run() {
						try {
							Thread.sleep(1000);
							System.out.println("done!" + Thread.currentThread().getId());
							final String data = "done!" + (new Date()).toString() + Thread.currentThread().getId();
							single.execute(new Runnable() {
								@Override
								public void run() {
									try {
										Thread.sleep(1000);
										System.out.println("data:" + data);
									}
									catch(Exception e) {
										e.printStackTrace();
									}
								}
							});
						}
						catch(Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
			// これで終わるまで待ちますね。
			System.out.println("done!");
			exec.shutdown();
			System.out.println("shutdownかけた");
			exec.awaitTermination(1000000, TimeUnit.MILLISECONDS);
			System.out.println("本当におわり");
			// ここにいれないと、singleのやつがおわったあとに追加されちゃうわけね。
			single.shutdown();
			single.awaitTermination(1000000, TimeUnit.SECONDS);
		}
		catch(Exception e) {
			logger.warn("例外が発生しました。", e);
		}
	}
}
