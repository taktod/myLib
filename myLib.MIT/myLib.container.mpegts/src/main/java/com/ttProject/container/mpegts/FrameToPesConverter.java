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
 * make pes from frame.
 * audioPes will be devided by set duration.
 * videoPes will be devided by keyFrame.
 * @author taktod
 */
public class FrameToPesConverter {
	/** logger */
	private Logger logger = Logger.getLogger(FrameToPesConverter.class);
	/** processed Pes holder map */
	private final Map<Integer, Pes> pesMap = new ConcurrentHashMap<Integer, Pes>();
	/** duration for audioPes. */
	private final float audioPesDuration;
	/**
	 * constructor
	 */
	public FrameToPesConverter() {
		this(0.3f);
	}
	/**
	 * constructor
	 * @param audioDuration
	 */
	public FrameToPesConverter(float audioDuration) {
		audioPesDuration = audioDuration;
	}
	/**
	 * make pes from frame.
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
		throw new Exception("found neither video nor audio frame." + frame.toString());
	}
	/**
	 * make new pes.
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
					throw new Exception("unexpected stream Id, need to start from non-zero.");
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
	 * get pes.
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
	 * getAudioPes
	 * @param frame
	 * @return
	 * @throws Exception
	 */
	private Pes getAudioPes(int pid, Pmt pmt, AudioFrame frame) throws Exception {
		Pes pes = getPes(pid, pmt);
		pes.addFrame(frame);
		IAudioFrame audioFrame = (IAudioFrame)pes.getFrame();
		// get duration from holding audioFrame sampleNum.
		if(1.0f * audioFrame.getSampleNum() / audioFrame.getSampleRate() > audioPesDuration) {
			// make new pes and register.
			makeNewPes(pid, pmt);
			return pes;
		}
		return null;
	}
	/**
	 * getVideoPes
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
	 * getH264Pes
	 * @param pid
	 * @param pmt
	 * @param frame
	 * @return
	 * @throws Exception
	 */
	private Pes getH264Pes(int pid, Pmt pmt, H264Frame frame) throws Exception {
		// for h264, deal with sliceFrame only.
		if(!(frame instanceof SliceFrame)) {
			return null;
		}
		Pes pes = pesMap.get(pid);
		// first data should be sliceIDR.
		pes = makeNewPes(pid, pmt);
		pes.addFrame(frame); // complete with only one slice frame.
		pesMap.remove(pid); // no more reuse, remove from map.
		return pes;
	}
	public Map<Integer, Pes> getPesMap() {
		return pesMap;
	}
}
