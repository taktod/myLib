/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.ogg;

import java.io.FileOutputStream;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ttProject.container.IContainer;
import com.ttProject.container.IWriter;
import com.ttProject.container.ogg.type.Page;
import com.ttProject.container.ogg.type.StartPage;
import com.ttProject.frame.CodecType;
import com.ttProject.frame.IAudioFrame;
import com.ttProject.frame.IFrame;
import com.ttProject.frame.IVideoFrame;
import com.ttProject.frame.extra.AudioMultiFrame;
import com.ttProject.frame.extra.VideoMultiFrame;
import com.ttProject.frame.speex.SpeexFrame;
import com.ttProject.frame.speex.type.CommentFrame;
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit5;
import com.ttProject.unit.extra.bit.Bit8;

/**
 * ogg page writer.
 * @author taktod
 * 
 * こちらの動作としては、つぎつぎにframeデータをいれていくが、frameデータがいっぱいになったら次のoggPageに移動しないとだめ。
 * 現在のページにつぎつぎとデータをいれていく。
 */
public class OggPageWriter implements IWriter {
	/** logger */
	private Logger logger = Logger.getLogger(OggPageWriter.class);
	/**  */
	private Map<Integer, OggPage> pageMap = new HashMap<Integer, OggPage>();
	private final WritableByteChannel outputChannel;
	private FileOutputStream outputStream = null;
	/** speexのsample数を足していく(この動作はvorbisやtheoraの場合にかわってくるので、本来はここにあるべきではない。またマルチチャンネルの場合もこまったことになります。) */
	private long addedSampleNum = 0;
	private List<CodecType> targetCodecTypeList = new ArrayList<CodecType>();
	private Map<Integer, CodecType> processTrackMap = new HashMap<Integer, CodecType>();
	/**
	 * constructor
	 * @param fileName
	 * @throws Exception
	 */
	public OggPageWriter(String fileName) throws Exception {
		outputStream = new FileOutputStream(fileName);
		this.outputChannel = outputStream.getChannel();
	}
	public OggPageWriter(FileOutputStream fileOutputStream) {
		this.outputChannel = fileOutputStream.getChannel();
	}
	public OggPageWriter(WritableByteChannel outputChannel) {
		this.outputChannel = outputChannel;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addContainer(IContainer container) throws Exception {
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addFrame(int trackId, IFrame frame) throws Exception {
		if(frame instanceof AudioMultiFrame) {
			for(IAudioFrame af : ((AudioMultiFrame) frame).getFrameList()) {
				addFrame(trackId, af);
			}
			return;
		}
		if(frame instanceof VideoMultiFrame) {
			for(IVideoFrame vf : ((VideoMultiFrame) frame).getFrameList()) {
				addFrame(trackId, vf);
			}
			return;
		}
		if(!processTrackMap.containsKey(trackId)) {
			// first frame for this track.
			// need to be setup.
			if(!targetCodecTypeList.remove(frame.getCodecType())) {
				logger.warn("non target frame is detected.:" + frame.getCodecType());
				return;
			}
			processTrackMap.put(trackId, frame.getCodecType());
			switch(frame.getCodecType()) {
			case SPEEX:
				SpeexFrame sFrame = (SpeexFrame)frame;
				addFrame(trackId, sFrame.getHeaderFrame());
				completePage(trackId);
				addFrame(trackId, new CommentFrame());
				completePage(trackId);
				break;
			case VORBIS:
				break;
			default:
				throw new Exception(frame.getCodecType() + " ogg writer is not supported now.");
			}
		}
		// TODO for the first frame, need to setup something. (headerFrame, commentFrame, setupFrame for vorbis like this.)
		if(frame instanceof IAudioFrame) {
			IAudioFrame aFrame = (IAudioFrame)frame;
			addedSampleNum += aFrame.getSampleNum();
			OggPage targetPage = null;
			if(pageMap.get(trackId) == null) {
				targetPage = new StartPage(new Bit8(), new Bit1(), new Bit1(1), new Bit1(), new Bit5());
				targetPage.setStreamSerialNumber(trackId);
				pageMap.put(trackId, targetPage);
			}
			else {
				targetPage = pageMap.get(trackId);
			}
			// set the data on page.
			targetPage.getFrameList().add(frame); // after add frame, need to update size.
			if(targetPage.getFrameList().size() >= 255) {
				completePage(trackId);
			}
			return;
		}
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void prepareHeader(CodecType ...codecs) throws Exception {
		// hold the target codecType.
		for(CodecType codec : codecs) {
			targetCodecTypeList.add(codec);
		}
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void prepareTailer() throws Exception {
		// set end the remain page.
		for(Integer key : pageMap.keySet()) {
			logger.info("target:" + key);
			OggPage page = pageMap.get(key);
			// update absoluteGranulePosition
			page.setAbsoluteGranulePosition(addedSampleNum);
			// set logic end flg
			page.setLogicEndFlag(true);
			// write pages.
			outputChannel.write(page.getData());
		}
		// close stream.
		if(outputStream != null) {
			try {
				outputStream.close();
			}
			catch(Exception e) {
			}
			outputStream = null;
		}
	}
	/**
	 * set the current page complete.
	 */
	public void completePage(int trackId) throws Exception {
		logger.info("force pageComplete");
		OggPage page = pageMap.get(trackId);
		logger.info(page.getClass());
		// update granulePosition(time position)
		page.setAbsoluteGranulePosition(addedSampleNum);
		// update output channel.
		outputChannel.write(page.getData());
		int lastSequenceNo = page.getPageSequenceNo();
		// prepare for next page.
		page = new Page(new Bit8(), new Bit1(), new Bit1(), new Bit1(), new Bit5());
		page.setStreamSerialNumber(trackId);
		page.setPageSequenceNo(lastSequenceNo + 1);
		pageMap.put(trackId, page);
	}
}
