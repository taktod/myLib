/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.flv;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ttProject.container.flv.type.AudioTag;
import com.ttProject.container.flv.type.VideoTag;
import com.ttProject.frame.AudioFrame;
import com.ttProject.frame.IFrame;
import com.ttProject.frame.VideoFrame;
import com.ttProject.frame.aac.AacFrame;
import com.ttProject.frame.aac.type.Frame;
import com.ttProject.frame.h264.H264Frame;
import com.ttProject.frame.h264.type.PictureParameterSet;
import com.ttProject.frame.h264.type.SequenceParameterSet;
import com.ttProject.frame.h264.type.Slice;
import com.ttProject.frame.h264.type.SliceIDR;
import com.ttProject.frame.h264.type.SupplementalEnhancementInformation;

/**
 * make flvTag from frame.
 * @author taktod
 */
public class FrameToFlvTagConverter {
	/** logger */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(FrameToFlvTagConverter.class);
	private ByteBuffer aacPrivateData = null;
	private SequenceParameterSet sps = null;
	private PictureParameterSet  pps = null;
	/** last audioTag */
	private AudioTag audioTag = null;
	/** last videoTag */
	private VideoTag videoTag = null;
	/**
	 * get the tags from frame.
	 * FlvTagリストを取得します。
	 * @return
	 */
	public List<FlvTag> getTags(IFrame frame) throws Exception {
		if(frame instanceof VideoFrame) {
			return getVideoTags((VideoFrame)frame);
		}
		else if(frame instanceof AudioFrame) {
			return getAudioTags((AudioFrame)frame);
		}
		throw new Exception("neither audio nor video frame?:" + frame.toString());
	}
	/**
	 * audioFrame.
	 * @param frame
	 * @return
	 */
	private List<FlvTag> getAudioTags(AudioFrame frame) throws Exception {
		List<FlvTag> result = new ArrayList<FlvTag>();
		// for aac we need to check msh.
		if(frame instanceof AacFrame) {
			Frame aacFrame = (Frame) frame;
			ByteBuffer privateData = aacFrame.getPrivateData();
			if(privateData.equals(aacPrivateData)) {
				aacPrivateData = privateData;
				AudioTag audioTag = new AudioTag();
				audioTag.setAacMediaSequenceHeader(aacFrame, aacPrivateData.duplicate());
				result.add(audioTag);
			}
		}
		// make audioTag.
		AudioTag audioTag = new AudioTag();
		audioTag.addFrame(frame);
		result.add(audioTag);
		return result;
	}
	/**
	 * videoframe.
	 * @param frame
	 * @return
	 */
	private List<FlvTag> getVideoTags(VideoFrame frame) throws Exception {
		// for h264, use other way.
		if(frame instanceof H264Frame) {
			return getH264Tags(frame);
		}
		List<FlvTag> result = new ArrayList<FlvTag>();
		// make videoTag.
		videoTag = new VideoTag();
		videoTag.addFrame(frame);
		result.add(videoTag);
		return result;
	}
	/**
	 * h264Frame
	 * @param frame
	 * @return
	 * @throws Exception
	 */
	private List<FlvTag> getH264Tags(VideoFrame frame) throws Exception {
		List<FlvTag> result = new ArrayList<FlvTag>();
		if(frame instanceof SupplementalEnhancementInformation) {
			// sei will be the cause of nullpointerexception.
			return result;
		}
		// check msh.
		if(frame instanceof SliceIDR) {
			SliceIDR sliceIDR = (SliceIDR)frame;
			if(sps == null || pps == null
			|| sps.getData().compareTo(sliceIDR.getSps().getData()) != 0
			|| pps.getData().compareTo(sliceIDR.getPps().getData()) != 0) {
				sps = sliceIDR.getSps();
				pps = sliceIDR.getPps();
				VideoTag videoTag = new VideoTag();
				videoTag.setH264MediaSequenceHeader(sliceIDR, sps, pps);
				result.add(videoTag);
			}
		}
		if(sps == null || pps == null) {
			// neither sps nor pps exists, no more task.
			return result;
		}
		if(frame instanceof Slice) {
			Slice slice = (Slice)frame;
			if(slice.getFirstMbInSlice() == 0) {
				if(videoTag != null) {
					result.add(videoTag);
				}
				videoTag = new VideoTag();
			}
			videoTag.addFrame(frame);
		}
		if(frame instanceof SliceIDR) {
			SliceIDR sliceIDR = (SliceIDR)frame;
			if(sliceIDR.getFirstMbInSlice() == 0) {
				if(videoTag != null) {
					result.add(videoTag);
				}
				videoTag = new VideoTag();
			}
			videoTag.addFrame(frame);
		}
		return result;
	}
	public VideoTag getRemainVideoTag() {
		return videoTag;
	}
	public AudioTag getRemainAudioTag() {
		return audioTag;
	}
}
