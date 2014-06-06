/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 */
package com.ttProject.license;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;

import org.apache.log4j.Logger;

/**
 * ライセンス記述を記載するプログラム
 * @author taktod
 */
public class WriteTest {
	/** ロガー */
	private Logger logger = Logger.getLogger(WriteTest.class);
	/**
	 * javaファイルの先頭にライセンス記述を書き込むプログラム
	 */
//	@Test
	public void javaDoc() {
		logger.info("処理テスト開始");
		// まずファイルをサーチする必要あり。
		File dir = new File("../../myLib.GPLv3");
		searchDir(dir);
		logger.info("処理テストおわり");
	}
	private void searchDir(File dir) {
		if(!dir.isDirectory()) {
			checkJava(dir);
			return;
		}
		for(File f : dir.listFiles()) {
			searchDir(f);
		}
	}
	private void checkJava(File f) {
		if(!f.getName().endsWith(".java")) {
			return;
		}
		else {
			logger.info(f);
//			writeLicense(f);
		}
	}
	private void writeLicense(File f) {
		// ファイルを確認して、先頭がライセンスでなければライセンス条文を挿入する必要あり。
		String license = "/*\n" + 
" * myLib - https://github.com/taktod/myLib\n" + 
" * Copyright (c) 2014 ttProject. All rights reserved.\n" + 
" * \n" + 
" * Licensed under GNU GENERAL PUBLIC LICENSE Version 3.\n" + 
" */\n";
		FileOutputStream fos = null;
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(f));
			String firstLine = br.readLine();
			if(firstLine.equals("/*")){
				// すでにライセンスが入力済み
				return;
			}
			// ライセンスがはいっていないので、いれる必要あり。
			File ftmp = new File(f.getAbsolutePath() + "tmp");
			fos = new FileOutputStream(f.getAbsolutePath() + "tmp");
			fos.write(license.getBytes());
			fos.write((firstLine + "\n").getBytes());
			String line = null;
			while((line = br.readLine()) != null) {
				fos.write((line + "\n").getBytes());
			}
			fos.close();
			fos = null;
			br.close();
			br = null;
			ftmp.renameTo(f);
		}
		catch(Exception e) {
		}
		finally {
			if(fos != null) {
				try {
					fos.close();
					fos = null;
				}
				catch(Exception e) {
				}
			}
			if(br != null) {
				try {
					br.close();
					br = null;
				}
				catch(Exception e) {
				}
			}
		}
	}
}
