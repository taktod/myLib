/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.chunk.mpegts.analyzer;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import com.ttProject.chunk.mpegts.AudioDataList;
import com.ttProject.chunk.mpegts.VideoDataList;
import com.ttProject.media.Unit;
import com.ttProject.media.h264.frame.AccessUnitDelimiter;
import com.ttProject.media.h264.frame.PictureParameterSet;
import com.ttProject.media.h264.frame.SequenceParameterSet;
import com.ttProject.media.h264.frame.Slice;
import com.ttProject.media.h264.frame.SliceIDR;
import com.ttProject.media.mpegts.CodecType;
import com.ttProject.media.mpegts.field.PmtElementaryField;
import com.ttProject.media.mpegts.packet.Pes;
import com.ttProject.media.mpegts.packet.Pmt;

/**
 * h264のframeを解析してPesを作成します。
 * IAudioDataはスルー
 * 強制pcrとなります。
 * @author taktod
 */
public class H264PesAnalyzer implements IPesAnalyzer {
	/** 動作ロガー */
	private Logger logger = Logger.getLogger(H264PesAnalyzer.class);
	/** 動作pmt(pesをつくるときに、pcr判定やpid等が必要になります。) */
	private Pmt pmt = null;
	/** 動作pid */
	private short pid;
	/** 分岐用のaccessUnitDelimiter */
	private AccessUnitDelimiter aud = new AccessUnitDelimiter();
	/** sps */
	private SequenceParameterSet sps = null;
	/** pps */
	private PictureParameterSet pps = null;
	/** pesデータ投入先 */
	private VideoDataList videoDataList = null;
	/**
	 * データの解析を実行します。
	 */
	@Override
	public void analyze(Unit unit, long timestamp) {
		if(unit instanceof Pmt) {
			if(pmt != null) { // すでにpmt解析済みなら捨てます。
				return;
			}
			try {
				pmt = (Pmt)unit;
				for(PmtElementaryField field : pmt.getFields()) {
					if(field.getCodecType() != CodecType.VIDEO_H264) {
						continue;
					}
					// 問題のトラックをみつけた。
					pid = field.getPid();
					break;
				}
			}
			catch(Exception e) {
				logger.error("pmt解析時に例外発生", e);
			}
		}
		else {
			try {
				if(unit instanceof Slice) {
					// innerFrame
					Slice slice = (Slice) unit;
					ByteBuffer audData = aud.getData();
					ByteBuffer sliceData = slice.getData();
					ByteBuffer buffer = ByteBuffer.allocate(
							  4 + audData.remaining()
							+ 4 + sliceData.remaining());
					buffer.putInt(1);
					buffer.put(audData);
					buffer.putInt(1);
					buffer.put(sliceData);
					buffer.flip();
					addByteBuffer(buffer, true, timestamp);
				}
				else if(unit instanceof SliceIDR) {
					// keyFrame
					SliceIDR sliceIDR = (SliceIDR)unit;
					ByteBuffer audData = aud.getData();
					ByteBuffer spsData = sps.getData();
					ByteBuffer ppsData = pps.getData();
					ByteBuffer sliceIDRData = sliceIDR.getData();
					ByteBuffer buffer = ByteBuffer.allocate(
							  4 + audData.remaining()
							+ 4 + spsData.remaining()
							+ 4 + ppsData.remaining()
							+ 4 + sliceIDRData.remaining());
					buffer.putInt(1);
					buffer.put(audData);
					buffer.putInt(1);
					buffer.put(spsData);
					buffer.putInt(1);
					buffer.put(ppsData);
					buffer.putInt(1);
					buffer.put(sliceIDRData);
					buffer.flip();
					addByteBuffer(buffer, true, timestamp);
				}
				else if(unit instanceof SequenceParameterSet) {
					sps = (SequenceParameterSet) unit;
				}
				else if(unit instanceof PictureParameterSet) {
					pps = (PictureParameterSet) unit;
				}
			}
			catch(Exception e) {
				logger.error("h264 nal解析中に例外が発生", e);
			}
		}
	}
	/**
	 * bufferをpesに分解して登録する動作
	 * @param buffer
	 * @param keyFrame
	 * @param startPts
	 * @throws Exception
	 */
	private void addByteBuffer(ByteBuffer buffer, boolean keyFrame, long startPts) throws Exception {
		Pes videoPes = new Pes(CodecType.VIDEO_H264, keyFrame, keyFrame, pid, buffer, startPts);
		do {
			// 空bufferの取得を実施して、内部のbyfferデータポインターを移動させる。
			videoPes.getBuffer();
			videoDataList.addPes(videoPes);
		} while((videoPes = videoPes.nextPes()) != null);
	}
	/**
	 * audioDataListを登録
	 */
	@Override
	public void setAudioDataList(AudioDataList audioDataList) {
	}
	/**
	 * videoDataListを登録
	 */
	@Override
	public void setVideoDataList(VideoDataList videoDataList) {
		this.videoDataList = videoDataList;
	}
}
