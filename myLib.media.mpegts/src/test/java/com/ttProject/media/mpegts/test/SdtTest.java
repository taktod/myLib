package com.ttProject.media.mpegts.test;

import org.junit.Test;

import com.ttProject.media.mpegts.descriptor.ServiceDescriptor;
import com.ttProject.media.mpegts.packet.Sdt;
import com.ttProject.util.HexUtil;

/**
 * sdtを生成するときの動作テスト
 * @author taktod
 */
public class SdtTest {
	/**
	 * sdtのデータ確認動作テスト
	 * @throws Exception
	 */
//	@Test
	public void check() throws Exception {
		Sdt sdt = new Sdt(HexUtil.makeBuffer("474011100042F0240001C100000001FF0001FC8013481101054C696261760953657276696365303168C5DB49"));
		System.out.println(sdt);
	}
	@Test
	public void test() throws Exception {
		Sdt sdt = new Sdt();
		sdt.writeDefaultProvider("Libav", "Service01");
		System.out.println(sdt);
		System.out.println(HexUtil.toHex(sdt.getBuffer(), true));
	}
	/**
	 * descriptorの書き込みテスト
	 * @throws Exception
	 */
	public void descriptor() throws Exception {
		ServiceDescriptor descriptor = new ServiceDescriptor();
		descriptor.setName("taktod", "test");
		System.out.println(descriptor);
	}
}
