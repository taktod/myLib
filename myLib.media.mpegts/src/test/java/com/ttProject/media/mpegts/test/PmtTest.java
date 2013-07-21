package com.ttProject.media.mpegts.test;

import org.junit.Test;

import com.ttProject.media.mpegts.packet.Pmt;
import com.ttProject.util.HexUtil;

/**
 * pmtの動作確認用テスト
 * @author taktod
 */
public class PmtTest {
	@Test
	public void check() throws Exception {
		Pmt pmt = new Pmt(HexUtil.makeBuffer("475000100002B0170001C10000E100F0001BE100F0000FE101F0002F44B99B"));
		System.out.println(pmt);
	}
}
