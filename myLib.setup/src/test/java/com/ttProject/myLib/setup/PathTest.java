package com.ttProject.myLib.setup;

import java.io.File;

import org.junit.Test;

/**
 * テスト
 * @author taktod
 *
 */
public class PathTest {
	@Test
	public void test() {
		File f = new File("test.flv");
		File ff = new File(f.getAbsolutePath());
		System.out.println(ff.getParentFile().getParentFile());
	}
}
