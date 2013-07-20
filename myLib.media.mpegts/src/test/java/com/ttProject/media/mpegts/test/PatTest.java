package com.ttProject.media.mpegts.test;

import org.junit.Test;

import com.ttProject.media.mpegts.packet.Pat;
import com.ttProject.util.HexUtil;

/**
 * patの動作確認用テスト
 * @author taktod
 *
 */
public class PatTest {
	public void check() throws Exception {
		Pat pat = new Pat(HexUtil.makeBuffer("474000100000B00D0001C100000001F0002AB104B2"));
		System.out.println(pat);
	}
	@Test
	public void test() throws Exception {
		Pat pat = new Pat();
		System.out.println(pat);
		System.out.println(HexUtil.toHex(pat.getBuffer(), true));
	}
}
