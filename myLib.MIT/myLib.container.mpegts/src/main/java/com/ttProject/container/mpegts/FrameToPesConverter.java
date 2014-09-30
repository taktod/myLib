/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.mpegts;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.ttProject.container.mpegts.field.PmtElementaryField;
import com.ttProject.container.mpegts.type.Pes;
import com.ttProject.container.mpegts.type.Pmt;
import com.ttProject.frame.AudioFrame;
import com.ttProject.frame.IAudioFrame;
import com.ttProject.frame.IFrame;
import com.ttProject.frame.VideoFrame;
import com.ttProject.frame.h264.H264Frame;
import com.ttProject.frame.h264.SliceFrame;

/**
 * frameデータからPesを作成するコンバーター
 * 音声のpesは設定したdurationごとにする。
 * 映像のpesはkeyFrameごとにする。
 * @author taktod
 */
public class FrameToPesConverter {
	/** ロガー */
	private Logger logger = Logger.getLogger(FrameToPesConverter.class);
	/** いままでの中途処理データ保持 */
	private final Map<Integer, Pes> pesMap = new ConcurrentHashMap<Integer, Pes>();
	/** audioデータのpesごとの長さ設定 */
	private final float audioPesDuration;
	/**
	 * コンストラクタ
	 */
	public FrameToPesConverter() {
		this(0.3f);
	}
	/**
	 * コンストラクタ
	 * @param audioDuration
	 */
	public FrameToPesConverter(float audioDuration) {
		audioPesDuration = audioDuration;
	}
	/**
	 * frameからpesを作成して応答します
	 * @param pid
	 * @param frame
	 * @return
	 * @throws Exception
	 */
	public Pes getPeses(int pid, Pmt pmt, IFrame frame) throws Exception {
		logger.info("add frame:" + frame);
		if(frame instanceof VideoFrame) {
			return getVideoPes(pid, pmt, (VideoFrame)frame);
		}
		else if(frame instanceof AudioFrame) {
			return getAudioPes(pid, pmt, (AudioFrame)frame);
		}
		throw new Exception("音声でも映像でもないフレームを検知しました。" + frame.toString());
	}
	/**
	 * pesを新たに作成します
	 * @param pid
	 * @param pmt
	 * @return
	 * @throws Exception
	 */
	private Pes makeNewPes(int pid, Pmt pmt) throws Exception {
		logger.info("make pes:" + Integer.toHexString(pid));
		Pes pes = new Pes(pid, pmt.getPcrPid() == pid);
		for(PmtElementaryField peField : pmt.getFields()) {
			if(pid == peField.getPid()) {
				if(pes.getStreamId() != 0) {
					throw new Exception("streamIdが始めから設定されることはないはずです。");
				}
				else {
					pes.setStreamId(peField.getSuggestStreamId());
				}
				break;
			}
		}
		pesMap.put(pid, pes);
		return pes;
	}
	/**
	 * pesを取得します。
	 * @param pid
	 * @param pmt
	 * @return
	 * @throws Exception
	 */
	private Pes getPes(int pid, Pmt pmt) throws Exception {
		Pes pes = pesMap.get(pid);
		if(pes == null) {
			pes = makeNewPes(pid, pmt);
		}
		return pes;
	}
	/**
	 * 音声フレームについて処理します
	 * @param frame
	 * @return
	 * @throws Exception
	 */
	private Pes getAudioPes(int pid, Pmt pmt, AudioFrame frame) throws Exception {
		Pes pes = getPes(pid, pmt);
		pes.addFrame(frame);
		IAudioFrame audioFrame = (IAudioFrame)pes.getFrame();
		// 保持しているsample数からデータの長さを割り出します。
		if(1.0f * audioFrame.getSampleNum() / audioFrame.getSampleRate() > audioPesDuration) {
			// 新たなpesを作成して登録しておきます。
			makeNewPes(pid, pmt);
			return pes;
		}
		return null;
	}
	/**
	 * 映像フレームについて処理します
	 * @param pid
	 * @param pmt
	 * @param frame
	 * @return
	 * @throws Exception
	 */
	private Pes getVideoPes(int pid, Pmt pmt, VideoFrame frame) throws Exception {
		if(frame instanceof H264Frame) {
			return getH264Pes(pid, pmt, (H264Frame)frame);
		}
		return null;
	}
	/**
	 * h264のフレームについて処理します
	 * @param pid
	 * @param pmt
	 * @param frame
	 * @return
	 * @throws Exception
	 */
	private Pes getH264Pes(int pid, Pmt pmt, H264Frame frame) throws Exception {
		// h264FrameはsliceFrame以外必要ない。
		if(!(frame instanceof SliceFrame)) {
			return null;
		}
		Pes pes = pesMap.get(pid);
		// 始めのデータがsliceIdrでない場合は処理しない
		pes = makeNewPes(pid, pmt);
		pes.addFrame(frame); // sliceFrameを１ついれればそれで完了するはず。
		pesMap.remove(pid); // 使い回すことはないので、mapから外しておく。
		return pes;
	}
	public Map<Integer, Pes> getPesMap() {
		return pesMap;
	}
}
