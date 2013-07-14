package com.ttProject.media.extra.test;

import org.junit.Test;

import com.ttProject.media.extra.Bit;
import com.ttProject.media.extra.Bit1;
import com.ttProject.media.extra.Bit2;
import com.ttProject.media.extra.Bit3;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;

public class BitTest {
	@Test
	public void test() throws Exception {
		IReadChannel channel = new ByteReadChannel(new byte[] {
				0x4f
		});
		Bit1 a = new Bit1();
		Bit2 b = new Bit2();
		Bit3 c = new Bit3();
		Bit2 d = new Bit2();
		Bit.bitLoader(channel, a, b, c, d);
		System.out.println(a);
		System.out.println(b);
		System.out.println(c);
		System.out.println(d);
		channel.close();
	}
}
