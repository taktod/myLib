package com.ttProject.transcode.ffmpeg.test;

import java.util.HashSet;
import java.util.Set;

/**
 * lockの動作テスト
 * @author taktod
 *
 */
public class LockTest {
	Set<String> set = new HashSet<String>();
//	@Test
	public void test() throws Exception {
		// これやると帰ってこなくなる。
		// よって先にlockかかっている必要がある。
//		synchronized(set) {
//			set.wait();
//		}
//		System.out.println("おわり");
	}
}
