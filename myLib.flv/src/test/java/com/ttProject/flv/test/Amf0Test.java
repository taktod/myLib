package com.ttProject.flv.test;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import com.ttProject.media.flv.amf.Amf0Value;
import com.ttProject.util.HexUtil;

/**
 * amf0用のデータがきちんとできているか動作テスト
 * @author taktod
 */
public class Amf0Test {
//	@Test
	public void dataTest() throws Exception {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("test", 132.12);
		ByteBuffer buf = Amf0Value.getValueBuffer(data);
		byte[] dat = new byte[buf.remaining()];
		buf.get(dat);
		System.out.println(HexUtil.toHex(dat, true));
	}
}
