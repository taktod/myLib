/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.transcode.ffmpeg.test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * executorをつかって無限ループする場合の動作テストやってみる。
 * @author taktod
 */
public class ExecutorLoopTest {
	private int count = 10;
//	@Test
	public void test() {
		final ExecutorService exec = Executors.newSingleThreadExecutor();
		if(exec instanceof ThreadPoolExecutor) {
			System.out.println(((ThreadPoolExecutor)exec).getActiveCount());
		}
		else {
			System.out.println(exec.getClass());
		}
		exec.execute(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(100);
				}
				catch(Exception e) {
					e.printStackTrace();
				}
				System.out.println("here");
				count --;
				if(count != 0) {
					exec.execute(this);
				}
			}
		});
		try {
			Thread.sleep(500);
			exec.shutdown(); // 止めたくなったらこれ以上queueが入らないようにしちゃう。
		}
		catch(Exception e) {
			
		}
		System.out.println("here...");
	}
}
