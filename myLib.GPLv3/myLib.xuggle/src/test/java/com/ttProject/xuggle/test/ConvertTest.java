/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 */
package com.ttProject.xuggle.test;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import com.ttProject.media.flv.CodecType;
import com.ttProject.media.flv.FlvHeader;
import com.ttProject.media.flv.tag.VideoTag;
import com.ttProject.util.DateUtil;
import com.ttProject.util.HexUtil;
import com.xuggle.xuggler.IAudioSamples;
import com.xuggle.xuggler.IAudioSamples.Format;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IPixelFormat;
import com.xuggle.xuggler.IRational;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.IVideoPicture;
import com.xuggle.xuggler.IStreamCoder.Direction;
import com.xuggle.xuggler.IStreamCoder.Flags;
import com.xuggle.xuggler.video.ConverterFactory;
import com.xuggle.xuggler.video.IConverter;

/**
 * データのコンバート動作のテスト
 * @author taktod
 */
public class ConvertTest {
	private Logger logger = Logger.getLogger(ConvertTest.class);
	/**
	 * h263のコンバート動作テスト とりあえず画像→h263をつくりたい。
	 * @throws Exception
	 */
//	@Test
	public void flv1() throws Exception {
		IStreamCoder coder = IStreamCoder.make(Direction.ENCODING, ICodec.ID.CODEC_ID_FLV1);
		IRational frameRate = IRational.make(15, 1); // 15fps
		coder.setNumPicturesInGroupOfPictures(30); // gopを30にしておく。keyframeが30枚ごとになる。
		
		coder.setBitRate(250000); // 250kbps
		coder.setBitRateTolerance(9000);
		coder.setPixelType(IPixelFormat.Type.YUV420P);
		coder.setWidth(320);
		coder.setHeight(240);
		coder.setGlobalQuality(10);
		coder.setFrameRate(frameRate);
		coder.setTimeBase(IRational.make(1, 1000)); // 1/1000設定(flvはこうなるべき)
		
		int retVal = coder.open(null, null);
		if(retVal < 0) {
			throw new RuntimeException("変換コーダーが開けませんでした。");
		}
		// 映像データを適当にコンバートするのに必要な処置
		IConverter converter = null;
		converter = ConverterFactory.createConverter(new BufferedImage(320, 240, BufferedImage.TYPE_3BYTE_BGR), IPixelFormat.Type.YUV420P);
		int index = 0;
		long firstTimestamp = -1;
		IPacket packet = IPacket.make();
		while(index < 100) {
			index ++;
			long now = System.currentTimeMillis();
			if(firstTimestamp == -1) {
				firstTimestamp = now;
			}
			BufferedImage image = image();
			IVideoPicture picture = converter.toPicture(image, 1000 * (now - firstTimestamp));
			retVal = coder.encodeVideo(packet, picture, 0);
			if(retVal < 0) {
				throw new Exception("変換失敗");
			}
			if(packet.isComplete()) {
				logger.info(packet);
				logger.info(HexUtil.toHex(packet.getByteBuffer(), true)); // flvに入るべきh263のデータ部分のみ入っています。
			}
			Thread.sleep((long)(1000 / frameRate.getDouble()));
		}
	}
	/**
	 * せっかくなので、flvとして書き込みしてみた。
	 */
//	@Test
	public void flv1MakeTest() {
		// flv1をつくりたいところだが・・・
		FileChannel output = null;
		try {
			// 書き込み対象ファイル作成
			output = new FileOutputStream("output.flv").getChannel();
			// headerの部分だけ書き込む必要あり。
			FlvHeader flvHeader = new FlvHeader();
			flvHeader.setVideoFlg(true);
			flvHeader.setAudioFlg(false);
			output.write(flvHeader.getBuffer());
			// xuggleの変換の準備エンコード処理
			IStreamCoder coder = IStreamCoder.make(Direction.ENCODING, ICodec.ID.CODEC_ID_FLV1);
			IRational frameRate = IRational.make(15, 1); // 15fps
			coder.setNumPicturesInGroupOfPictures(30); // gopを30にしておく。keyframeが30枚ごとになる。
			
			coder.setBitRate(250000); // 250kbps
			coder.setBitRateTolerance(9000);
			coder.setPixelType(IPixelFormat.Type.YUV420P);
			coder.setWidth(320);
			coder.setHeight(240);
			coder.setGlobalQuality(10);
			coder.setFrameRate(frameRate);
			coder.setTimeBase(IRational.make(1, 1000)); // 1/1000設定(flvはこうなるべき)
			
			int retVal = coder.open(null, null);
			if(retVal < 0) {
				throw new RuntimeException("変換コーダーが開けませんでした。");
			}
			// 画像データを変換可能なpixelデータに変換する。
			IConverter converter = null;
			converter = ConverterFactory.createConverter(new BufferedImage(320, 240, BufferedImage.TYPE_3BYTE_BGR), IPixelFormat.Type.YUV420P);

			int index = 0; // 処理ステップ数
			long firstTimestamp = -1; // 時間のコントロール
			IPacket packet = IPacket.make(); // 処理パケット
			while(index < 100) {
				index ++;
				long now = System.currentTimeMillis();
				if(firstTimestamp == -1) {
					firstTimestamp = now;
				}
				// 表示させる画像を作成
				BufferedImage image = image();
				// BufferedImage→picture
				IVideoPicture picture = converter.toPicture(image, 1000 * (now - firstTimestamp));
				// picture→videoデータ(エンコード)
				retVal = coder.encodeVideo(packet, picture, 0);
				if(retVal < 0) {
					throw new Exception("変換失敗");
				}
				// パケットが完成した場合
				if(packet.isComplete()) {
					// flvデータをつくってみる。
					VideoTag videoTag = new VideoTag();
					videoTag.setCodec(CodecType.H263);
					// キーフレーム判定
					videoTag.setFrameType(packet.isKey());
					// 実データ取得
					ByteBuffer buffer = packet.getByteBuffer();
					videoTag.setSize(12 + 4 + buffer.remaining());
					videoTag.setTimestamp((int)(now - firstTimestamp));
					videoTag.setRawData(buffer);
					output.write(videoTag.getBuffer());
				}
				// 次のフレームの処理する前にしばらく時間をあける。
				Thread.sleep((long)(1000 / frameRate.getDouble()));
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			Assert.fail("例外が発生しました。");
		}
		finally {
			if(output != null) {
				try {
					output.close();
				}
				catch (Exception e) {
				}
			}
		}
	}
	/**
	 * avc(h264)の変換テスト
	 * @throws Exception
	 */
//	@Test
	public void avc() throws Exception {
		IStreamCoder coder = IStreamCoder.make(Direction.ENCODING, ICodec.ID.CODEC_ID_H264);
		IRational frameRate = IRational.make(15, 1); // 15fps
		coder.setNumPicturesInGroupOfPictures(30); // gopを30にしておく。keyframeが30枚ごとになる。
		
		coder.setBitRate(250000); // 250kbps
		coder.setBitRateTolerance(9000);
		coder.setPixelType(IPixelFormat.Type.YUV420P);
		coder.setWidth(320);
		coder.setHeight(240);
		coder.setGlobalQuality(10);
		coder.setFrameRate(frameRate);
		coder.setTimeBase(IRational.make(1, 1000)); // 1/1000設定(flvはこうなるべき)
		coder.setProperty("level", "30");
		coder.setProperty("coder", "0");
		coder.setProperty("qmin", "10");
		coder.setProperty("bf", "0");
		coder.setProperty("wprefp", "0");
		coder.setProperty("cmp", "+chroma");
		coder.setProperty("partitions", "-parti8x8+parti4x4+partp8x8+partp4x4-partb8x8");
		coder.setProperty("me_method", "hex");
		coder.setProperty("subq", "5");
		coder.setProperty("me_range", "16");
		coder.setProperty("keyint_min", "25");
		coder.setProperty("sc_threshold", "40");
		coder.setProperty("i_qfactor", "0.71");
		coder.setProperty("b_strategy", "0");
		coder.setProperty("qcomp", "0.6");
		coder.setProperty("qmax", "30");
		coder.setProperty("qdiff", "4");
		coder.setProperty("directpred", "0");
		coder.setProperty("cqp", "0");
		coder.setFlag(Flags.FLAG_LOOP_FILTER, true);
		coder.setFlag(Flags.FLAG_CLOSED_GOP, true);
		
		int retVal = coder.open(null, null);
		if(retVal < 0) {
			throw new RuntimeException("変換コーダーが開けませんでした。");
		}
		// 映像データを適当にコンバートするのに必要な処置
		IConverter converter = null;
		converter = ConverterFactory.createConverter(new BufferedImage(320, 240, BufferedImage.TYPE_3BYTE_BGR), IPixelFormat.Type.YUV420P);
		int index = 0;
		long firstTimestamp = -1;
		IPacket packet = IPacket.make();
		while(index < 100) {
			index ++;
			long now = System.currentTimeMillis();
			if(firstTimestamp == -1) {
				firstTimestamp = now;
			}
			BufferedImage image = image();
			IVideoPicture picture = converter.toPicture(image, 1000 * (now - firstTimestamp));
			retVal = coder.encodeVideo(packet, picture, 0);
			if(retVal < 0) {
				throw new Exception("変換失敗");
			}
			if(packet.isComplete()) {
				logger.info(packet);
				logger.info(HexUtil.toHex(packet.getByteBuffer(), true)); // flvに入るべきh263のデータ部分のみ入っています。
				/*
				 * すべてnal構造で応答されることと、はじめのキーフレームデータにsps ppsがはいっているらしいので、単純には、flv化できないっぽい。
00 00 00 01 67 64 00 1E AC B2 02 83 F4 20 00 00 03 00 20 00 00 FA 01 E2 C5 C9
00 00 00 01 68 CB CF 2C
00 00 01 06 05 FF FF 6C DC 45 E9 BD E6 D9 48 B7 96 2C D8 20 D9 23 EE EF 78 32 36 34 20 2D 20 63 6F 72 65 20 31 32 32 20 72 33 35 39 36 38 2B 36 32 4D 20 32 61 61 32 31 35 34 20 2D 20 48 2E 32 36 34 2F 4D 50 45 47 2D 34 20 41 56 43 20 63 6F 64 65 63 20 2D 20 43 6F 70 79 6C 65 66 74 20 32 30 30 33 2D 32 30 31 32 20 2D 20 68 74 74 70 3A 2F 2F 77 77 77 2E 76 69 64 65 6F 6C 61 6E 2E 6F 72 67 2F 78 32 36 34 2E 68 74 6D 6C 20 2D 20 6F 70 74 69 6F 6E 73 3A 20 63 61 62 61 63 3D 30 20 72 65 66 3D 33 20 64 65 62 6C 6F 63 6B 3D 31 3A 30 3A 30 20 61 6E 61 6C 79 73 65 3D 30 78 33 3A 30 78 31 33 33 20 6D 65 3D 68 65 78 20 73 75 62 6D 65 3D 35 20 70 73 79 3D 31 20 70 73 79 5F 72 64 3D 31 2E 30 30 3A 30 2E 30 30 20 6D 69 78 65 64 5F 72 65 66 3D 31 20 6D 65 5F 72 61 6E 67 65 3D 31 36 20 63 68 72 6F 6D 61 5F 6D 65 3D 31 20 74 72 65 6C 6C 69 73 3D 31 20 38 78 38 64 63 74 3D 31 20 63 71 6D 3D 30 20 64 65 61 64 7A 6F 6E 65 3D 32 31 2C 31 31 20 66 61 73 74 5F 70 73 6B 69 70 3D 31 20 63 68 72 6F 6D 61 5F 71 70 5F 6F 66 66 73 65 74 3D 30 20 74 68 72 65 61 64 73 3D 36 20 73 6C 69 63 65 64 5F 74 68 72 65 61 64 73 3D 30 20 6E 72 3D 30 20 64 65 63 69 6D 61 74 65 3D 31 20 69 6E 74 65 72 6C 61 63 65 64 3D 30 20 62 6C 75 72 61 79 5F 63 6F 6D 70 61 74 3D 30 20 63 6F 6E 73 74 72 61 69 6E 65 64 5F 69 6E 74 72 61 3D 30 20 62 66 72 61 6D 65 73 3D 30 20 77 65 69 67 68 74 70 3D 32 20 6B 65 79 69 6E 74 3D 33 30 20 6B 65 79 69 6E 74 5F 6D 69 6E 3D 31 36 20 73 63 65 6E 65 63 75 74 3D 34 30 20 69 6E 74 72 61 5F 72 65 66 72 65 73 68 3D 30 20 72 63 5F 6C 6F 6F 6B 61 68 65 61 64 3D 33 30 20 72 63 3D 61 62 72 20 6D 62 74 72 65 65 3D 31 20 62 69 74 72 61 74 65 3D 32 35 30 20 72 61 74 65 74 6F 6C 3D 31 2E 30 20 71 63 6F 6D 70 3D 30 2E 36 30 20 71 70 6D 69 6E 3D 31 30 20 71 70 6D 61 78 3D 33 30 20 71 70 73 74 65 70 3D 34 20 69 70 5F 72 61 74 69 6F 3D 31 2E 34 30 20 61 71 3D 31 3A 31 2E 30 30 00 80 
00 00 01 65 88 84 11 EF FF F8 78 3E 28 00 08 6F F9 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 39 3A EB AE BA EB AE BA EB AE BA EB AE BA EB AE BA EB AE BA EB AE BA EB AE BA EB AE BA EB AE BA EB AE BA EB AE BA EB AE BA EB AE BA EB AE BA EB AE BA EB AE BA EB AE BA EB AE BA EB AE BA EB AE BA EB DF FF 42 6B 0F 70 01 58 9A 0D 48 22 5A 83 D5 EC 0E 88 C2 D8 44 B4 7E 54 7F FF DC 3A 48 8C 01 87 28 0A 37 38 74 91 90 03 0C 5A 83 6D 5F 00 00 0F C0 C6 24 74 7E 38 F8 47 D0 DC 00 3C 39 3D EC C5 29 9F FF 63 43 75 FE 4F DB 27 BD AF C4 F8 1A 08 5F F0 01 62 FC BC 84 E4 07 3C 71 FA EB 01 59 65 81 0F 79 A4 6A 2D FB 9E B5 F7 55 FF FD E0 73 48 35 28 A9 4B 46 61 FF EF 3F 75 F4 D3 DF 76 80 F0 2F FF CE 28 48 9E C7 E2 36 55 BF FF 06 31 EE F8 54 BF 84 FD B8 9A 0D 48 22 5A 83 D5 E0 38 45 21 10 BA 10 FC 5F BF 83 20 00 80 3A 10 7B AE 10 09 D9 02 F8 01 51 22 20 04 1C D2 C1 42 3B CE 02 51 A1 92 D1 62 D4 D4 57 FB EF C0 01 9B 44 99 9B 21 22 AA 78 13 37 E1 90 90 A0 54 59 9E FF E0 76 A8 56 38 CC 76 3F DF CF E3 46 1C D6 B3 22 E7 2F F0 13 BD BC 79 32 E4 DD E0 ED CF FC 31 BF A2 16 91 95 54 51 3C E4 37 00 21 AA 55 B4 A4 96 57 61 B0 C0 03 C7 F7 06 45 25 29 3D 20 87 CF 84 22 6D A4 39 73 18 01 F9 B6 66 B8 C1 1C 21 69 47 FF 83 0C 30 24 BA F4 C2 A0 B4 1C 9D 5F BF A0 B0 58 84 2A F2 80 E8 47 F7 CB 13 C1 F5 B1 F7 C0 56 59 70 0F A2 84 73 EF 34 B5 6B EE 1F FF FF 01 1B 44 99 9B 21 22 AA 68 85 A4 65 55 14 4F 39 0D DA 26 C6 FC 84 1D 1A 83 C8 F7 FF EA 68 D2 32 D8 95 1D 70 C1 38 9C D8 C5 73 75 6B AD 46 90 68 41 52 A4 1E AF 71 C6 3D AF 46 6F 1D FF B1 4A 55 55 37 FF FF E0 6F 83 15 9F A9 86 01 3B E0 08 6A 95 6D 29 25 95 D8 60 F0 02 A2 44 40 08 39 A5 82 04 77 9C 04 A3 43 25 A2 C5 A9 BD 5F EF B9 B6 66 B8 C1 14 21 69 57 FF 83 0F E0 2C DF 86 42 42 81 51 66 4F B8 B5 42 B1 C6 63 A9 FE FE 58 7F 4F C1 01 43 80 B9 F9 B6 1A 36 7C C9 FE 0E FC F0 C3 C2 44 E0 02 D9 06 82 15 A9 49 6D 1D E7 D8 16 A4 43 52 96 D1 5C EF F7 BE FC E1 9B 53 7C 00 E0 99 10 02 0E 59 81 B6 A2 C0 00 40 2C 12 98 01 65 34 E3 B8 7D 5F FF 00 15 99 18 78 44 35 28 37 6A 04 22 D9 1B 4D 91 6A D2 A0 73 F3 F9 E8 8E FE FB ED AC C2 C3 1F 11 8D 3F FC FF DC 73 09 80 A4 37 C0 05 8B F2 F2 13 90 1C F1 C7 EB A0 15 96 58 10 F7 9A 46 A2 DF BE 7A D7 DD 57 FF F0 60 73 48 35 28 A9 4B 46 61 FF EF 34 D3 DF 76 80 F0 2F FF CD D7 0F 8A 12 27 B1 F8 8D 95 6F FF D4 4C 0E E2 11 0A 01 47 3E B9 86 3F 37 1F 13 64 E1 7C 00 1D 9B 12 50 25 5E BD EF D0 CF C8 10 06 A0 A5 05 CB E0 02 C5 F9 79 09 C8 0E 78 E3 F5 D6 3C 88 2E 41 10 C4 0D 4D FF FF 76 AC 9B 24 DB 23 95 80 20 12 29 28 46 4D 80 00 40 4F 59 75 6C 46 A2 A6 29 7F BF 78 1C D2 0D 4A 2A 52 D1 9A 7F FB CF DD 7B 13 C6 68 20 6E 01 7C 05 65 96 04 3D E6 91 A8 B7 EE 7A D7 DD 57 FF F7 EB 48 D6 46 18 DD BE 5B BD E9 93 09 17 23 8E 46 3F 4D 3D F7 68 0F 02 FF FC E2 84 89 EC 7E 23 65 5B FF F0 63 1F FA 29 C6 5A 34 9D D1 25 49 25 55 F8 1C C0 00 43 1E 62 65 AF DA DF 84 FF 08 E4 C7 48 81 E4 B5 67 03 E4 D0 9D EA B9 4B 1F 37 F8 3A C2 11 8B 4A 99 7E 7A EB AE BA EB D6 89 2F FF 09 60 00 FC 66 29 71 2F 5C 72 7F 43 B7 B2 00 44 E2 20 4E 15 37 6B A1 D1 61 A0 05 94 A2 82 B7 87 4F 16 FF E1 28 60 00 E1 7B 52 F3 D7 23 F2 8D FD 0F 43 4E B8 32 19 47 AB 92 BF 7D AF 85 17 B6 9A AB FF 7D FF 0E 09 FF E1 28 70 4C 88 01 07 2C C0 DB 50 74 91 0C 39 65 18 1B 6A FF 03 18 73 1A 26 0F 64 AF 37 FD FF AE 16 3F FE 12 80 14 26 64 00 C3 1A 58 28 47 79 DC 70 7B 08 CE 9C 27 72 8D FB C3 C5 29 22 55 24 FD F7 81 0D E4 68 99 37 1A DE 1C 65 C4 3F F0 97 82 68 95 5A 74 65 5E DF DC 07 E4 49 CF 1C AF C8 37 F0 61 86 1E 00 F2 72 80 64 8E 02 FA CF CE D9 9F FF 09 68 34 AB 08 61 29 AF 7F DF F1 B7 67 8B 6D 58 89 BD FF CB 79 34 46 4D C6 B7 04 D1 2A B6 E8 CA BC 9F C1 87 16 17 0F FC 25 E0 3F 22 4E 78 EE FC 83 7F 06 1C 00 A1 33 20 06 18 D2 C1 02 3B CE E3 83 D8 46 74 E1 3B 94 5F DE 1C DF 00 87 F0 F7 55 49 22 A4 AB FE FF F8 1C 00 80 38 29 95 88 4C 3F F0 96 07 00 20 0E 0A 64 00 A9 22 30 06 1C A0 28 DC FC 05 04 C8 80 10 72 CC 0D B5 01 09 E4 99 A2 6F 25 BC 3A 30 6F FF 09 54 10 04 80 A5 98 2E 44 00 10 07 31 97 E8 C9 1E 80 7C B0 A8 B5 FD CF E3 1D 8F FF 84 A0 04 85 ED 4B CF 5C 8F CA 37 F6 86 9D 70 64 32 8F 56 25 7E F8 3F B3 71 1B 0B 59 CA 7F BA 01 01 21 FE 6F 59 7A 0E CB C3 AD CF FF 84 BC 6C C8 0F 9B D9 9C A1 FF FD C3 24 7A 01 D5 2C 2A 2D 06 18 60 00 81 7B 52 FA 7A E4 7E E3 43 85 B0 18 7F C2 5D 0D 3A E0 C8 65 1E 8C 4A 7D F6 BE 14 5E DA 6A AF FD F7 F8 61 C0 08 03 8E 41 E2 FF FF 0F 02 10 13 B1 BB A9 CE A5 1D 29 4C 86 0F 5D 75 D7 5D 75 D7 5D 75 D7 5D 75 D7 5D 77 D7 5D 75 D7 5D 75 D7 5D 75 D7 5D 75 D7 5D 75 D7 5D 75 D7 5D 75 D7 5D 75 D7 5D 75 D7 5D 75 D7 5D 75 D7 5D 75 D7 5D 75 D7 5D 75 D7 5D 75 D7 5D 75 D7 5D 75 D7 5D 75 D7 5D 75 D7 5D 75 D7 5D 75 D7 5D 75 D7 5D 75 D7 5D 75 D7 5D 75 D7 5D 75 D7 5D 75 D7 5D 75 D7 5D 75 D7 5D 75 D7 5D 75 D7 5D 75 D7 5D 75 D7 5D 75 D7 5D 75 D7 80 
				 */
			}
			Thread.sleep((long)(1000 / frameRate.getDouble()));
		}
	}
//	@Test
	public void avcMakeTest() {
		// avcの動画をつくりたいところ・・・
		FileChannel output = null;
		try {
			// 書き込み対象ファイル作成
			output = new FileOutputStream("output.flv").getChannel();
			// headerの部分だけ書き込む必要あり。
			FlvHeader flvHeader = new FlvHeader();
			flvHeader.setVideoFlg(true);
			flvHeader.setAudioFlg(false);
			output.write(flvHeader.getBuffer());
			// xuggleの変換の準備エンコード処理
			IStreamCoder coder = IStreamCoder.make(Direction.ENCODING, ICodec.ID.CODEC_ID_H264);
			IRational frameRate = IRational.make(15, 1); // 15fps
			coder.setNumPicturesInGroupOfPictures(30); // gopを30にしておく。keyframeが30枚ごとになる。
			
			coder.setBitRate(250000); // 250kbps
			coder.setBitRateTolerance(9000);
			coder.setPixelType(IPixelFormat.Type.YUV420P);
			coder.setWidth(320);
			coder.setHeight(240);
			coder.setGlobalQuality(10);
			coder.setFrameRate(frameRate);
			coder.setTimeBase(IRational.make(1, 1000)); // 1/1000設定(flvはこうなるべき)
			coder.setProperty("level", "30");
			coder.setProperty("coder", "0");
			coder.setProperty("qmin", "10");
//			coder.setProperty("async", "4");
			coder.setProperty("bf", "0");
			coder.setProperty("wprefp", "0");
			coder.setProperty("cmp", "+chroma");
			coder.setProperty("partitions", "-parti8x8+parti4x4+partp8x8+partp4x4-partb8x8");
			coder.setProperty("me_method", "hex");
			coder.setProperty("subq", "5");
			coder.setProperty("me_range", "16");
			coder.setProperty("keyint_min", "25");
			coder.setProperty("sc_threshold", "40");
			coder.setProperty("i_qfactor", "0.71");
			coder.setProperty("b_strategy", "0");
			coder.setProperty("qcomp", "0.6");
			coder.setProperty("qmax", "30");
			coder.setProperty("qdiff", "4");
			coder.setProperty("directpred", "0");
			coder.setProperty("cqp", "0");
			coder.setFlag(Flags.FLAG_LOOP_FILTER, true);
			coder.setFlag(Flags.FLAG_CLOSED_GOP, true);			
			int retVal = coder.open(null, null);
			if(retVal < 0) {
				throw new RuntimeException("変換コーダーが開けませんでした。");
			}
			// 画像データを変換可能なpixelデータに変換する。
			IConverter converter = null;
			converter = ConverterFactory.createConverter(new BufferedImage(320, 240, BufferedImage.TYPE_3BYTE_BGR), IPixelFormat.Type.YUV420P);

			int index = 0; // 処理ステップ数
			long firstTimestamp = -1; // 時間のコントロール
			IPacket packet = IPacket.make(); // 処理パケット
			while(index < 100) {
				index ++;
				long now = System.currentTimeMillis();
				if(firstTimestamp == -1) {
					firstTimestamp = now;
				}
				// 表示させる画像を作成
				BufferedImage image = image();
				// BufferedImage→picture
				IVideoPicture picture = converter.toPicture(image, 1000 * (now - firstTimestamp));
				// picture→videoデータ(エンコード)
				retVal = coder.encodeVideo(packet, picture, 0);
				if(retVal < 0) {
					throw new Exception("変換失敗");
				}
				// パケットが完成した場合
				if(packet.isComplete()) {
					// flvデータをつくってみる。
					VideoTag videoTag = new VideoTag();
					// TODO codecTypeの設定まちがってない？
					videoTag.setCodec(CodecType.H263);
					// キーフレーム判定
					videoTag.setFrameType(packet.isKey());
					// 実データ取得
					ByteBuffer buffer = packet.getByteBuffer();
					videoTag.setSize(12 + 4 + buffer.remaining());
					videoTag.setTimestamp((int)(now - firstTimestamp));
					videoTag.setRawData(buffer);
					output.write(videoTag.getBuffer());
				}
				// 次のフレームの処理する前にしばらく時間をあける。
				Thread.sleep((long)(1000 / frameRate.getDouble()));
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			Assert.fail("例外が発生しました。");
		}
		finally {
			if(output != null) {
				try {
					output.close();
				}
				catch (Exception e) {
				}
			}
		}
	}
	/**
	 * 表示画像をつくってみる。
	 * @return
	 */
	private BufferedImage image() {
		BufferedImage base = new BufferedImage(320, 240, BufferedImage.TYPE_3BYTE_BGR);
		String message = DateUtil.makeDateTime();
		Graphics g = base.getGraphics();
		g.setColor(Color.white);
		g.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 24));
		g.drawString(message, 100, 100);
		g.dispose();
		return base;
	}
	/**
	 * 生音からmp3をつくる動作テスト
	 * @throws Exception
	 */
	@Test
	public void mp3() throws Exception {
		// FMT_S16というのもあるんだが・・・こちらはbigendianだったりしないかなぁ
//		IAudioSamples samples = IAudioSamples.make(1024, 2, Format.FMT_S16); // 1024サンプルを２チャンネルでもっておく。
		FileChannel outputMp3 = new FileOutputStream("output.mp3").getChannel();
		// TODO なんかサンプル数がおかしいけど、おいといて、とりあえず変換してみよう。
		IStreamCoder coder = IStreamCoder.make(Direction.ENCODING, ICodec.ID.CODEC_ID_MP3);
		coder.setSampleRate(44100);
		coder.setChannels(2);
		coder.setBitRate(96000);
		if(coder.open(null, null) < 0) {
			throw new Exception("変換コーダーが開けませんでした。");
		}
		// 直接データをつくってsamplesに書き込めばいいのかな・・・
		int samplingRate = 44100;
		int length = 10; // 1秒分
		int tone = 440; // ラの音
		int bit = 16; // FMT_S16なので16ビット
		ByteBuffer buffer = ByteBuffer.allocate((int)(1024 * bit * 2 / 8));
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		double rad = tone * 2 * Math.PI / samplingRate;
		double max = (1 << (bit - 2)) - 1;
		IPacket packet = IPacket.make();
		for(int i = 0;i < samplingRate * length;i ++) {
			short data = (short)((Math.sin(rad * i) * max));
			buffer.putShort(data); // 左右同じデータにしてやる
			buffer.putShort(data); // 左右同じデータにしてやる
			if(buffer.limit() == buffer.position()) {
				// 最後までいったら今回のbufferはたまったことになる。
				buffer.flip();
//				logger.info(buffer.remaining());
//				logger.info(buffer.array().length);
				IAudioSamples samples = IAudioSamples.make(1024, 2, Format.FMT_S16);
				byte[] ddd = buffer.array();
				logger.info(ddd.length);
				logger.info(samples.getData());
				samples.getData().put(buffer.array(), 0, 0, buffer.remaining());
//				IBuffer bufff = IBuffer.make(null, Type.IBUFFER_SINT16, 1024, false);
//				IBuffer bufff = IBuffer.make(null, buffer.array(), 0, 5000); // 大きめを設定しておくときちんと動作できるのだろうか
//				logger.info("こっち？:" + bufff);
//				samples.setData(bufff);
//				logger.info("ここか？:" + samples.getData());
				// 1/1000000の状態で音声用のtimestampがはいっていないとだめ
				// 1024 / 44100が時間になるのか・・・
				samples.setComplete(true, 1024, samplingRate, 2, Format.FMT_S16, 0);
				logger.info(samples);
				int samplesConsumed = 0;
				while(samplesConsumed < samples.getNumSamples()) {
					logger.info(samples.getNumSamples() - samplesConsumed);
					int retval = coder.encodeAudio(packet, samples, samplesConsumed);
					if(retval < 0) {
						throw new Exception("変換失敗");
					}
					samplesConsumed += retval;
					if(packet.isComplete()) {
						logger.info(packet);
//						logger.info(packet.getSize());
//						logger.info(HexUtil.toHex(packet.getData().getByteArray(0, packet.getSize()), true));
						outputMp3.write(packet.getData().getByteBuffer(0, packet.getSize()));
					}
				}
				buffer = ByteBuffer.allocate((int)(1024 * bit * 2 / 8));
				buffer.order(ByteOrder.LITTLE_ENDIAN);
			}
		}
		if(coder != null) {
			coder.close();
			coder = null;
		}
		if(outputMp3 != null) {
			outputMp3.close();
			outputMp3 = null;
		}
//		buffer.flip();
//		IAudioSamples samples = IAudioSamples.make(IBuffer.make(null, buffer.array(), 0, buffer.remaining()), 2, Format.FMT_S16);
//		samples.setComplete(true, numSamples, sampleRate, channels, format, pts)
//		logger.info(samples);
//		samples.setComplete(complete, numSamples, sampleRate, channels, format, pts)
		// あとlittleEndianに注意かも
	}
}
