/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.packet.mpegts.test;

import java.io.FileOutputStream;
import java.nio.ByteBuffer;

import org.apache.log4j.Logger;
import org.junit.Assert;

import com.ttProject.media.mpegts.IPacketAnalyzer;
import com.ttProject.media.mpegts.Packet;
import com.ttProject.media.mpegts.PacketAnalyzer;
import com.ttProject.media.mpegts.packet.Pes;
import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.packet.IMediaPacket;
import com.ttProject.packet.mpegts.MpegtsPacket;
import com.ttProject.packet.mpegts.MpegtsPacketManager;
import com.ttProject.util.BufferUtil;

/**
 * ファイルの読み込みを実行するテスト
 * @author taktod
 * １：とりあえずファイルの読み込みテストをつくる。
 * ２：mpegtsのデータをパケット分けして保持できるようにしておく。
 * h264のkeyFrameChunk
 * h264のinnerFrameのchunk
 * aacのchunk
 * patのchunk
 * pmtのchunk
 * sdtのchunk
 * 
 * ３：実験によると、音声のchunkがある程度の大きさにならないと送られてこないっぽいです。
 * 音声chunkが大きすぎることがありそうです。
 * よって、音声chunkは分解してやってきちんとしたサイズにしてやった方が動作は安定しそうです。
 * ただし、分解するには、そこまでのデータが転送されてこないと作業できなくなります。
 * 実験では、10秒転送されてくるまで、データがこないので無音部があると動作がわるくなりそう・・・
 * ま、とりあえず現状の動作では、そこまでずれることはなさそうなので分解して書き直すことはしないことにします。
 * 
 * 通常の部分では、0.1秒ほど遅れる感じで音声データが挿入されるっぽいですが・・・うーん。
 * とりあえずなるべく完璧なデータになって欲しいところ。
 * 分解できそうなのは、音声パケットなので(映像パケットは分解しようがない。)音声パケットがくるまでひたすらまって
 * 必要な時間に分解しようとおもいます。
 * とりあえずaacとmp3のみ受け入れることにします。
 * HeaderPacketとMediaPacketは内部用
 * MpegtsPacketとして、必要なデータ長分応答すればよさそうです。
 * mpegtsPacketManagerは受け入れのところ。
 */
public class FileLoadTest {
	/** ロガー */
	private final Logger logger = Logger.getLogger(FileLoadTest.class);
//	@Test
	public void readTest() {
		logger.info("ファイル読み込みテスト開始");
		IReadChannel source = null;
		try {
			source = FileReadChannel.openFileReadChannel(
					Thread.currentThread().getContextClassLoader().getResource("mario.ts")
			);
			IPacketAnalyzer analyzer = new PacketAnalyzer();
			Packet packet = null;
			while((packet = analyzer.analyze(source)) != null) {
				if(packet instanceof Pes) { 
					if(packet.isPayloadUnitStart()) {
						Pes pes = (Pes)packet;
						if(pes.isAdaptationFieldExist()) {
							logger.info(pes.getAdaptationField());
						}
						if(pes.getPid() == 0x0101) {
							logger.info("*" + pes.getPts());
//							Thread.sleep(100);
						}
						else {
							logger.info(pes.getPts());
						}
					}
				}
			}
		}
		catch(Exception e) {
			logger.error("", e);
		}
		finally {
			if(source != null) {
				try {
					source.close();
				}
				catch(Exception e) {
				}
				source = null;
			}
		}
	}
	/**
	 * パケットをつくる動作テスト
	 * @throws Exception
	 */
//	@Test
	public void test() throws Exception {
		logger.info("パケット生成テスト開始");
		IReadChannel source = null;
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream("hogehoge2.ts");
			source = FileReadChannel.openFileReadChannel(
					Thread.currentThread().getContextClassLoader().getResource("mario.ts")
			);
			MpegtsPacketManager packetManager = new MpegtsPacketManager();
			packetManager.setDuration(10); // 5秒ごとに設定しておく。
			// 読み込めたデータを送り込んでいけばOK
			ByteBuffer buffer = null;
			while(true) {
				int remaining = source.size() - source.position();
				int readSize = (remaining > 655350 ? 655350 : remaining);
				if(readSize == 0) {
					logger.info("読み込みサイズが0になりました。");
					break;
				}
				logger.info("読み込みます。");
				buffer = BufferUtil.safeRead(source, readSize);
				// 読み取ったbufferをぶち込む
				for(IMediaPacket packet : packetManager.getPackets(buffer)) {
					logger.info(packet);
					System.out.println(packet);
					fos.write(packet.getRawData());
				}
			}
			// 残っているデータがあれば・・・
			MpegtsPacket packet = (MpegtsPacket)packetManager.getCurrentPacket();
			if(packet != null) {
				// パケットの生成が実行されていないので、ゴミが残ったままになってしまうわけか・・・
				logger.info("packetがまだあるので、書き込みしておきます。");
				fos.write(packet.getRawData());
			}
		}
		catch(Exception e) {
			logger.error("エラーが発生しました。", e);
			Assert.fail("例外が発生してしまいました。");
		}
		finally {
			if(fos != null) {
				try {
					fos.close();
				}
				catch(Exception e) {
				}
				fos = null;
			}
			if(source != null) {
				try {
					source.close();
				}
				catch(Exception e) {
				}
				source = null;
			}
		}
		
	}
}
