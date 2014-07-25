/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.flv;

import java.io.FileOutputStream;
import java.nio.channels.WritableByteChannel;
import java.util.List;

import org.apache.log4j.Logger;

import com.ttProject.container.IContainer;
import com.ttProject.container.IWriter;
import com.ttProject.container.flv.type.AudioTag;
import com.ttProject.container.flv.type.VideoTag;
import com.ttProject.frame.IAudioFrame;
import com.ttProject.frame.IFrame;
import com.ttProject.frame.IVideoFrame;
import com.ttProject.frame.extra.AudioMultiFrame;
import com.ttProject.frame.extra.VideoMultiFrame;

/**
 * flvの書き込み動作
 * @author taktod
 * 
 * ここですが、面倒なので、一旦frameからflvTag用のbyteBufferをつくってから、selectorで読み込ませるという形にしたいと思います。
 * flazrでやっているのと同じ、これだったら既存の処理の使い回しで済む
 */
public class FlvTagWriter implements IWriter {
	/** ロガー */
	private Logger logger = Logger.getLogger(FlvTagWriter.class);
	private final WritableByteChannel outputChannel;
	private FileOutputStream outputStream = null;

	/** frameをflvTagに変換するコンバーター */
	private FrameToFlvTagConverter frameConverter = new FrameToFlvTagConverter();
	public FlvTagWriter(String fileName) throws Exception {
		outputStream = new FileOutputStream(fileName);
		this.outputChannel = outputStream.getChannel();
	}
	public FlvTagWriter(FileOutputStream fileOutputStream) {
		this.outputChannel = fileOutputStream.getChannel();
	}
	public FlvTagWriter(WritableByteChannel outputChannel) {
		this.outputChannel = outputChannel;
	}
	@Override
	public void addContainer(IContainer container) throws Exception {
		logger.info("コンテナを受け取りました。:" + container);
		outputChannel.write(container.getData());
	}
	@Override
	public void addFrame(int trackId, IFrame frame) throws Exception {
		if(frame == null) {
			return;
		}
		if(frame instanceof VideoMultiFrame) {
			VideoMultiFrame multiFrame = (VideoMultiFrame)frame;
			for(IVideoFrame vFrame : multiFrame.getFrameList()) {
				addFrame(trackId, vFrame);
			}
			return;
		}
		// nellymoserの場合はmultiFrameの場合はそのままmultiFrameとしていれてしまった方が楽かもしれません。
		// とはいえ、mpegtsからコンバートするときみたいに、mp3もmultiFrameでくる可能性があるわけで、その場合は、分割してaudioTag化しないとだめ
		// 面倒な・・・
		if(frame instanceof AudioMultiFrame) {
			AudioMultiFrame multiFrame = (AudioMultiFrame)frame;
			for(IAudioFrame aFrame : multiFrame.getFrameList()) {
				addFrame(trackId, aFrame);
			}
			return;
		}
		// TODO h264の場合は複数のフレームで1つになることがあるらしい。
		List<FlvTag> tagList = frameConverter.getTags(frame);
		if(tagList != null) {
			for(FlvTag tag : tagList) {
				outputChannel.write(tag.getData());
			}
		}
	}
	@Override
	public void prepareHeader() throws Exception {
		logger.info("headerを準備します。");
	}
	@Override
	public void prepareTailer() throws Exception {
		logger.info("tailerを準備します。");
		AudioTag audioTag = frameConverter.getRemainAudioTag();
		if(audioTag != null) {
			outputChannel.write(audioTag.getData());
		}
		VideoTag videoTag = frameConverter.getRemainVideoTag();
		if(videoTag != null) {
			outputChannel.write(videoTag.getData());
		}
		// h264の場合はend tagをいれた方がよい。
		if(outputStream != null) {
			try {
				outputStream.close();
			}
			catch(Exception e) {
			}
			outputStream = null;
		}
	}
}
