/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.opus.test;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ttProject.container.IContainer;
import com.ttProject.container.Reader;
import com.ttProject.container.ogg.OggPage;
import com.ttProject.container.ogg.OggPageReader;
import com.ttProject.frame.IFrame;
import com.ttProject.frame.opus.OpusFrame;
import com.ttProject.frame.opus.type.CommentFrame;
import com.ttProject.frame.opus.type.Frame;
import com.ttProject.frame.opus.type.HeaderFrame;
import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.util.HexUtil;

/**
 * ogg(opus)のデコード処理をjavaで書いてみたい。
 * @author taktod
 * oggコンテナの解析を実施するために、javaのbuildパスとして、myLib.container.ogg-0.0.2-SNAPSHOT.jarを取り込んで動作させています。
 * mvnだけで解決できないので、注意(mavenの依存関係いれるとframe.opusとcontainer.oggの依存関係が巡回になってエラーになる)
 */
public class FileDecodeTest {
	/** ロガー */
	private Logger logger = Logger.getLogger(FileDecodeTest.class);
	/**
	 * 動作テスト
	 * @throws Exception
	 */
//	@Test
	public void test() throws Exception {
		IReadChannel source = FileReadChannel.openFileReadChannel("http://49.212.39.17/gc_25-1_no_3.ogg");
		Reader reader = new OggPageReader();
		IContainer container = null;
		while((container = reader.read(source)) != null) {
			if(container instanceof OggPage) {
				OggPage page = (OggPage)container;
				for(IFrame frame : page.getFrameList()) {
					if(frame instanceof OpusFrame) {
						analyzeOpusFrame((OpusFrame)frame);
					}
				}
			}
		}
	}
	/**
	 * 解析動作を実施しておきます。
	 * @param opusFrame
	 * とりあえず、opusのheaderFrameをglobalとして保存しておきつつ、通常のframeを抜き出していろいろやればいいと思う
	 */
	public void analyzeOpusFrame(OpusFrame opusFrame) throws Exception {
		if(opusFrame instanceof HeaderFrame) {
			// 内部的にはframeから取り出せるっぽいので、とりあえずスルーでいいと思う
		}
		else if(opusFrame instanceof CommentFrame) {
			// コメントフレームはメタ情報だけでデコードには寄与しないっぽいので放置する
		}
		else if(opusFrame instanceof Frame) {
			// 実際のデータがあるフレーム
			// こいつをなんとかしておく。
			Frame frame = (Frame)opusFrame;
			logger.info(HexUtil.toHex(frame.getData())); // フレームデータがきちんと取得できたみたいです。
			// さて解析するか・・・
			Thread.sleep(1000);
		}
	}
}
