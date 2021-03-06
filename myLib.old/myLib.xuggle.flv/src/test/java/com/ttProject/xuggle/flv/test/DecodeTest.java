/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 */
package com.ttProject.xuggle.flv.test;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import com.ttProject.media.flv.FlvHeader;
import com.ttProject.media.flv.ITagAnalyzer;
import com.ttProject.media.flv.Tag;
import com.ttProject.media.flv.TagAnalyzer;
import com.ttProject.media.flv.tag.AudioTag;
import com.ttProject.media.flv.tag.VideoTag;
import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IFileReadChannel;
import com.ttProject.xuggle.flv.FlvPacketizer;
import com.xuggle.xuggler.IAudioSamples;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.IVideoPicture;

/**
 * デコード動作のテスト
 * @author taktod
 *
 */
public class DecodeTest {
	private Logger logger = Logger.getLogger(DecodeTest.class);
	@Test
	public void h263Test() {
//		videoDecodeTest("/home/xxx/h263.flv");
	}
	@Test
	public void vp6Test() {
//		videoDecodeTest("/home/xxx/vp6.flv");
	}
	@Test
	public void avcTest() {
//		videoDecodeTest("/home/xxx/avc.flv");
	}
	@SuppressWarnings("unused")
	private void videoDecodeTest(String target) {
		try {
			IFileReadChannel source = FileReadChannel.openFileReadChannel(target);
			FlvHeader flvheader = new FlvHeader();
			flvheader.analyze(source);
			logger.info(flvheader);
			ITagAnalyzer analyzer = new TagAnalyzer();
			// sourceをそのまま解析する。
			FlvPacketizer packetizer = new FlvPacketizer();
			IStreamCoder decoder = null;
			IPacket packet = null;
			Tag tag = null;
			while((tag = analyzer.analyze(source)) != null) {
				if(tag instanceof VideoTag) {
					packet = packetizer.getPacket(tag, null);
					if(packet == null) {
						continue;
					}
					if(decoder == null) {
						decoder = packetizer.createVideoDecoder();
					}
					int offset = 0;
					IVideoPicture picture = IVideoPicture.make(decoder.getPixelType(), decoder.getWidth(), decoder.getHeight());
					while(offset < packet.getSize()) {
						int bytesDecoded = decoder.decodeVideo(picture, packet, offset);
						if(bytesDecoded <= 0) {
							throw new Exception("デコード中に問題が発生しました。");
						}
						offset += bytesDecoded;
						if(picture.isComplete()) {
							logger.info(picture);
						}
					}
				}
			}
			decoder.close();
			source.close();
		}
		catch (Exception e) {
			e.printStackTrace();
			Assert.fail("例外が発生しました。");
		}
	}
	@Test
	public void aacTest() {
		/*　TODO いつか直したところ
		 * いろいろ試したけど。やっぱりwindows8では、aacのデコードのみそのままだときちんと動作しないみたいです。
		 * で、動作させるにはですが・・・
		 * いったん別のデコーダーに大きなバッファをいれてエンコード成功させて閉じてしまうか
		 * 別のデコーダーに大きなバッファをいれてエンコード失敗させたまま放置しておいとくかのどちらかでしか動作可能にできない模様です。
		 * 失敗した場合はデコーダーを閉じてしまうとだめみたいです。
		 * 
		 * どうやらdecoderを２つつくって１つ目をdummyにしてしまえば大丈夫っぽいです。
		 */
//		audioDecodeTest("/home/xxx/aac.flv");
	}
	@Test
	public void mp3Test() {
//		audioDecodeTest("/home/xxx/mp3.flv");
	}
	@Test
	public void nellyTest() {
//		audioDecodeTest("/home/xxx/nelly.flv");
	}
	@Test
	public void speexTest() {
//		audioDecodeTest("/home/xxx/speex.flv");
	}
	@SuppressWarnings("unused")
	private void audioDecodeTest(String target) {
		try {
			IFileReadChannel source = FileReadChannel.openFileReadChannel(target);
			FlvHeader flvheader = new FlvHeader();
			flvheader.analyze(source);
			ITagAnalyzer analyzer = new TagAnalyzer();
			// sourceをそのまま解析する。
			FlvPacketizer packetizer = new FlvPacketizer();
			IStreamCoder decoder = null;
			IPacket packet = null;
			Tag tag = null;
			while((tag = analyzer.analyze(source)) != null) {
				if(tag instanceof AudioTag) {
					packet = packetizer.getPacket(tag, null);
					if(packet == null) {
						continue;
					}
					if(decoder == null) {
						decoder = packetizer.createAudioDecoder();
					}
					IAudioSamples samples = IAudioSamples.make(1024, decoder.getChannels());
					int offset = 0;
					while(offset < packet.getSize()) {
						int bytesDecoded = decoder.decodeAudio(samples, packet, offset);
						if(bytesDecoded < 0) {
							throw new Exception("デコード中にエラーが発生しました。");
						}
						offset += bytesDecoded;
						if(samples.isComplete()) {
							logger.info(samples);
						}
					}
				}
			}
			if(decoder != null) {
				decoder.close();
				decoder = null;
			}
			if(source != null) {
				source.close();
				source = null;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			Assert.fail("例外が発生しました。");
		}
	}
}
