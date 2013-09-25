package com.ttProject.myLib.setup;

import java.io.File;

/**
 * テスト
 * @author taktod
 *
 */
public class PathTest {
//	@Test
	public void test() {
		System.out.println(getTargetFile("a/b/c/d/test.flv"));
	}
	/**
	 * 
	 * @param path
	 * @param file
	 * @return
	 */
	public String getTargetFile(String file) {
		String[] data = file.split("/");
		File f = new File(".");
		f = new File(f.getAbsolutePath());
		f = f.getParentFile().getParentFile();
		for(String path : data) {
			f = new File(f.getAbsolutePath(), path);
		}
		f.getParentFile().mkdirs();
		return f.getAbsolutePath();
	}
}
