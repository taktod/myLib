/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.convertprocess.frame;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import com.ttProject.frame.AudioSelector;
import com.ttProject.frame.IAudioFrame;
import com.ttProject.frame.IFrame;
import com.ttProject.frame.IVideoFrame;
import com.ttProject.frame.VideoSelector;
import com.ttProject.unit.ISelector;
import com.ttProject.unit.extra.BitConnector;
import com.ttProject.unit.extra.bit.Bit32;
import com.ttProject.unit.extra.bit.Bit64;
import com.ttProject.unit.extra.bit.Bit8;
import com.ttProject.util.BufferUtil;

/**
 * プロセス間でframeを共有する場合の定義データ
 * データは次の形で保持します。
 * Bit8:codecType
 * Bit64:pts
 * Bit64:dts
 * Bit32:timebase
 * Bit32:trackId
 * 音声の場合
 * Bit32:sampleRate
 * Bit32:channels
 * Bit32:bitNum
 * sampleNum保持させるべきかな？
 * 映像の場合
 * Bit32:width
 * Bit32:height
 * 以下フレームデータ
 * このデータをやり取りするのはサーバー内部のみだと思うので、まぁサイズがおおきくなったりしても特に問題ないだろう・・・という考えからきています。
 * @author taktod
 */
public class ShareFrameData {
	/** ロガー */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(ShareFrameData.class);
	private Bit8  codecType      = new Bit8();
	private Bit64 pts            = new Bit64();
	private Bit64 dts            = new Bit64();
	private Bit32 timebase       = new Bit32();
	private Bit32 trackId        = new Bit32();
	private Bit32 sampleRate     = null;
	private Bit32 channels       = null;
	private Bit32 bitNum         = null;
	private Bit32 width          = null;
	private Bit32 height         = null;
	private CodecType  type      = null;
	private ByteBuffer frameData = null;
	/**
	 * コンストラクタ
	 * データから復元する
	 * @param data 共有データ
	 * @throws Exception
	 */
	public ShareFrameData(ByteBuffer data) throws Exception {
		codecType.set(data.get());
		type = CodecType.getCodecType(codecType.get());
		pts.setLong(data.getLong());
		dts.setLong(data.getLong());
		timebase.set(data.getInt());
		trackId.set(data.getInt());
		if(type.isAudio()) {
			sampleRate = new Bit32(data.getInt());
			channels = new Bit32(data.getInt());
			bitNum = new Bit32(data.getInt());
		}
		else {
			width = new Bit32(data.getInt());
			height = new Bit32(data.getInt());
			frameData = ByteBuffer.allocate(data.remaining());
			frameData.put(data);
			frameData.flip();
		}
	}
	public ShareFrameData(CodecType type, IFrame frame, int trackId) throws Exception {
		codecType.set(type.getValue());
		this.type = type;
		pts.setLong(frame.getPts());
		if(frame instanceof IVideoFrame) {
			dts.setLong(((IVideoFrame) frame).getDts());
		}
		timebase.set((int)frame.getTimebase());
		this.trackId.set(trackId);
		if(type.isAudio()) {
			IAudioFrame aFrame = (IAudioFrame)frame;
			sampleRate = new Bit32(aFrame.getSampleRate());
			channels = new Bit32(aFrame.getChannel());
			bitNum = new Bit32(aFrame.getBit());
		}
		else {
			IVideoFrame vFrame = (IVideoFrame)frame;
			width = new Bit32(vFrame.getWidth());
			height = new Bit32(vFrame.getHeight());
		}
	}
	/**
	 * frameのデフォルト値を設定しておきます
	 * @param selector
	 */
	public void setupFrameSelector(ISelector selector) {
		if(selector instanceof AudioSelector) {
			AudioSelector aSelector = (AudioSelector)selector;
			aSelector.setSampleRate(sampleRate.get());
			aSelector.setChannel(channels.get());
			aSelector.setBit(bitNum.get());
		}
		else if(selector instanceof VideoSelector) {
			VideoSelector vSelector = (VideoSelector)selector;
			vSelector.setWidth(width.get());
			vSelector.setHeight(height.get());
		}
	}
	/**
	 * pts応答
	 * @return
	 */
	public long getPts() {
		return pts.getLong();
	}
	/**
	 * dts応答
	 * @return
	 */
	public long getDts() {
		return dts.getLong();
	}
	/**
	 * timebase応答
	 * @return
	 */
	public int getTimebase() {
		return timebase.get();
	}
	/**
	 * trackId参照
	 * @return
	 */
	public int getTrackId() {
		return trackId.get();
	}
	/**
	 * codecType参照
	 * @return
	 */
	public CodecType getCodecType() {
		return type;
	}
	/**
	 * フレーム用データ応答
	 * @return
	 */
	public ByteBuffer getFrameData() {
		return frameData.duplicate();
	}
	/**
	 * frameDataを設定しておきます。
	 * @param buffer
	 */
	public void setFrameData(ByteBuffer buffer) {
		frameData = buffer;
	}
	public ByteBuffer getShareData() {
		BitConnector connector = new BitConnector();
		return BufferUtil.connect(
				connector.connect(codecType, pts, dts, timebase, trackId, sampleRate, channels, bitNum, width, height),
				frameData.duplicate());
	}
}
