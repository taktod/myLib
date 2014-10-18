/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.h264;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ttProject.frame.IVideoFrame;
import com.ttProject.frame.extra.VideoMultiFrame;
import com.ttProject.frame.h264.exception.ConfigDataException;
import com.ttProject.frame.h264.type.PictureParameterSet;
import com.ttProject.frame.h264.type.SequenceParameterSet;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.ISelector;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit16;
import com.ttProject.unit.extra.bit.Bit2;
import com.ttProject.unit.extra.bit.Bit3;
import com.ttProject.unit.extra.bit.Bit5;
import com.ttProject.unit.extra.bit.Bit6;
import com.ttProject.unit.extra.bit.Bit8;
import com.ttProject.util.BufferUtil;

/**
 * configData
 * from flv mediaSequenceHeader or mp4 decodeBox(Avcc)
 * nalデータを取り出す動作
 * 01 4D 40 1E FF E1 00 19 67 4D 40 1E 92 42 01 40 5F F2 E0 22 00 00 03 00 C8 00 00 2E D5 1E 2C 5C 90 01 00 04 68 EE 32 C8
 * 01 42 00 16 FF E1 00 2E 67 42 80 16 96 54 05 01 ED 80 A84000000300400000053800007A10000F425FC638C00003D080007A12FE31C3B4244D4001000468CE3520
 * 01 42 80 16 FF E1 00 2E 67 42 80 16 96 54 05 01 ED 80 A84000000300400000053800007A10000F425FC638C00003D080007A12FE31C3B4244D4001000468CE3520
 * [] avcC version 1
 *    [      ] profile compatibility level
 *             [] 111111 + 2bit nal size - 1(ff固定とおもっててOKでしょう)
 *                [] number of SPS e1固定？
 *                   [   ] spsLength
 *                         [spsNalデータ                                                             ]
 *                                                                                                    [] number of PPS
 *                                                                                                       [   ] ppsLength
 *                                                                                                             [         ] ppsData
 * もしくはnalデータからconfigDataを作り出す動作
 * 参照元のデータが古かった模様です。
 * @see http://blog.arcen.org/201109/article_1.html
 * こっちを参考にして組み直しておこう。
 * 01 4D 40 1E FF E1 00 19 67 4D 40 1E 92 42 01 40 5F F2 E0 22 00 00 03 00 C8 00 00 2E D5 1E 2C 5C 90 01 00 04 68 EE 32 C8
 * [] avcC version1(互換性がくずれたら新しい番号が振られるらしい、いまのところ1以外みたことない)
 *    [      ] profile compatibility levelの３点セット
 *             [] lengthSizeMinusOneWithReserved 0x3F | (nalLength - 1); (nalLengthは1,2,4のどれかっぽい)
 *                                      ここ・・・0xFCの間違いじゃないかな (注)
 *                [] numOfSequenceParameterSetsWithReserved 0xE0 | 数になる(この場合１つ)
 *                   [   ] spsの長さ
 *                         [] spsの本体
 *                   numOfSequenceParameterSetsWithReserved | 0x1Fの数分、spsが繰り返されます。(よってspsが複数ある場合を想定していないので、現状のmyLib.frame.h264は複数ある場合はつかえないですね。)
 * 01 4D 40 1E FF E1 00 19 67 4D 40 1E 92 42 01 40 5F F2 E0 22 00 00 03 00 C8 00 00 2E D5 1E 2C 5C 90 01 00 04 68 EE 32 C8
 *                                                                                                    [] numOfPictureParameterSets ppsの数
 *                                                                                                       [   ] ppsのサイズ
 *                                                                                                             [         ] ppsデータ
 *                                                                                                       spsと同じくppsの数分だけ繰り返されることになります。
 *                                                                                                                         profileの値が100 110 122 144の場合はさらにspse(sequenceParameterSetExtがあります。)
 * 注:lengthSizeMinusOneの部分は、flvやmp4のnalSize定義の部分に影響しているものと思われます。
 * 09 00 03 05 00 00 00 00 00 00 00 17 01 00 00 00 00 00 02 fc 65 88 80 80  0f ff 
 *                                                 [         ]この部分0x000002fcがNalのサイズになっていますが、
 *                                                 これはNalLengthMinusOneが3になっているので4byteになっているだけみたいです。
 * mkvやmp4、flvでlengthSizeMinusOneの部分が3の場合に4byteによるnalデータサイズ定義があることを確認しました。
 * これらのコンテナでは、ConfigDataを取り回して使わないとNalのサイズ決定ができないっぽいですね。
 * @author taktod
 */
public class ConfigData {
	private Logger logger = Logger.getLogger(ConfigData.class);
	/** h264 data selector */
	private H264FrameSelector selector = null;
	private Bit8 avcCVersion = new Bit8();
	private Bit8 profile         = new Bit8();
	private Bit8 compatibility   = new Bit8();
	private Bit8 level           = new Bit8();
	private Bit6 reservedBit1    = new Bit6();
	private Bit2 nalSizeMinusOne = new Bit2();

	private Bit3 reservedBit2    = new Bit3();
	private Bit5 numOfSps = new Bit5();
	private List<SequenceParameterSet> spsList = new ArrayList<SequenceParameterSet>();

	private Bit8 numOfPps = new Bit8();
	private List<PictureParameterSet> ppsList = new ArrayList<PictureParameterSet>();
	// sps ext(profile = 100? 110 122 144 required.)
/*		private Bit6 reservedBit3;
		private Bit2 chromaFormat;
		private Bit5 reservedBit4;
		private Bit3 bitDepthLumaMinus8;
		private Bit5 reservedBit5;
		private Bit3 bitDepthChromaMinus8;
		private Bit8 numOfSpse;
		private List<SequenceParameterSetExt> spseList; */
	/**
	 * set the selector
	 * @param selector
	 */
	public void setSelector(H264FrameSelector selector) {
		this.selector = selector;
	}
	/**
	 * ref the nal size for data nal.
	 * usually 4byte.
	 * @return
	 */
	public int getNalSizeBytes() {
		return nalSizeMinusOne.get() + 1;
	}
	/**
	 * ref the sps list.
	 * @return
	 */
	public List<SequenceParameterSet> getSpsList() {
		return new ArrayList<SequenceParameterSet>(spsList);
	}
	/**
	 * ref the pps list.
	 * @return
	 */
	public List<PictureParameterSet> getPpsList() {
		return new ArrayList<PictureParameterSet>(ppsList);
	}
	/**
	 * analyze configData.
	 * @param channel
	 * @throws Exception
	 */
	public void analyzeData(IReadChannel channel) throws Exception {
		ISelector selector = null;
		if(this.selector != null) {
			selector = this.selector;
		}
		else {
			selector = new H264FrameSelector();
		}
		// check first 5 byte.
		BitLoader loader = new BitLoader(channel);
		loader.load(avcCVersion, profile, compatibility, level, reservedBit1, nalSizeMinusOne);
		// get sps
		loader.load(reservedBit2, numOfSps);
		for(int i = 0;i < numOfSps.get();i ++) {
			Bit16 spsSize = new Bit16();
			loader.load(spsSize);
			logger.info("spsSize:" + spsSize.get());
			IReadChannel byteChannel = new ByteReadChannel(BufferUtil.safeRead(channel, spsSize.get()));
			IVideoFrame sps = (IVideoFrame)selector.select(byteChannel);
			if(sps == null) {
				throw new ConfigDataException("sps is missing, use nalAnalyzer.");
			}
			if(!(sps instanceof SequenceParameterSet)) {
				throw new Exception("sps is expected, however, found other frame.:" + sps.getClass().getSimpleName());
			}
			sps.load(byteChannel);
			spsList.add((SequenceParameterSet)sps);
		}
		// get pps
		loader.load(numOfPps);
		for(int i = 0;i < numOfPps.get();i ++) {
			Bit16 ppsSize = new Bit16();
			loader.load(ppsSize);
			IReadChannel byteChannel = new ByteReadChannel(BufferUtil.safeRead(channel, ppsSize.get()));
			IVideoFrame pps = (IVideoFrame)selector.select(byteChannel);
			if(!(pps instanceof PictureParameterSet)) {
				throw new Exception("pps is expected, however, found other frame.:" + pps.getClass().getSimpleName());
			}
			pps.load(byteChannel);
			ppsList.add((PictureParameterSet)pps);
		}
		switch(profile.get()) {
		case 100:
			break;
		case 110:
		case 122:
		case 144:
			throw new Exception("profile required spsext, however, not code yet.");
		default:
			break;
		}
	}
	/**
	 * get sps and pps.
	 * @param channel
	 * @return
	 * @throws Exception
	 */
	public IVideoFrame getNalsFrame(IReadChannel channel) throws Exception {
		ISelector selector = null;
		if(this.selector != null) {
			selector = this.selector;
		}
		else {
			selector = new H264FrameSelector();
		}
		VideoMultiFrame result = new VideoMultiFrame();
		if(channel.size() - channel.position() < 8) {
			throw new Exception("data size is too small");
		}
		ByteBuffer buffer = BufferUtil.safeRead(channel, 6);
		if(buffer.get() != 0x01) {
			throw new Exception("avccVersion is not 1, I need sample.");
		}
		short spsSize = BufferUtil.safeRead(channel, 2).getShort();
		IReadChannel byteChannel = new ByteReadChannel(BufferUtil.safeRead(channel, spsSize));
		IVideoFrame sps = (IVideoFrame)selector.select(byteChannel);
		if(!(sps instanceof SequenceParameterSet)) {
			throw new Exception("sps is expected, however, the frame is " + sps.getClass().getSimpleName());
		}
		sps.load(byteChannel);
		result.addFrame(sps);
		BufferUtil.safeRead(channel, 1);
		short ppsSize = BufferUtil.safeRead(channel, 2).getShort();
		byteChannel = new ByteReadChannel(BufferUtil.safeRead(channel, ppsSize));
		IVideoFrame pps = (IVideoFrame)selector.select(byteChannel);
		if(!(pps instanceof PictureParameterSet)) {
			throw new Exception("pps is expected, however, the frame is " + pps.getClass().getSimpleName());
		}
		pps.load(byteChannel);
		result.addFrame(pps);
		return result;
	}
	/**
	 * make configData from sps and pps.
	 * @param sps
	 * @param pps
	 * @return
	 * @throws Exception
	 */
	public ByteBuffer makeConfigData(SequenceParameterSet sps, PictureParameterSet pps) throws Exception {
		ByteBuffer spsBuffer = sps.getData();
		ByteBuffer ppsBuffer = pps.getData();
		ByteBuffer data = ByteBuffer.allocate(11 + spsBuffer.remaining() + ppsBuffer.remaining());
		data.put((byte)1);
		spsBuffer.position(1);
		int profile = spsBuffer.get() & 0xFF;
		byte compatibility = spsBuffer.get();
		data.put((byte)profile);
		switch(profile) {
		case 0x42:
			if(compatibility != (byte)0xE0) {
				compatibility = 0x00;
			}
			break;
		case 0x58:
			if(compatibility != (byte)0xA0) {
				compatibility = 0x00;
			}
			break;
		case 0x4D:
			if(compatibility != (byte)0x40) {
				compatibility = 0x00;
			}
			break;
		case 0x64:
			compatibility = 0x00;
			break;
		default:
			compatibility = 0x00;
			break;
		}
		data.put(compatibility);
		data.put(spsBuffer.get());
		spsBuffer.position(0);
		data.put((byte)0xFF);
		data.put((byte)0xE1);
		data.putShort((short)spsBuffer.remaining());
		data.put(spsBuffer);
		data.put((byte)1);
		data.putShort((short)ppsBuffer.remaining());
		data.put(ppsBuffer);
		data.flip();
		return data;
	}
}
