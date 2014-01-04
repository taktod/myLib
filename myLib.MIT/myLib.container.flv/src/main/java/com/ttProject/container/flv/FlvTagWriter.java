package com.ttProject.container.flv;

import java.io.FileOutputStream;
import java.nio.channels.WritableByteChannel;

import org.apache.log4j.Logger;

import com.ttProject.container.IContainer;
import com.ttProject.container.IWriter;
import com.ttProject.container.flv.type.AudioTag;
import com.ttProject.container.flv.type.VideoTag;
import com.ttProject.frame.IAudioFrame;
import com.ttProject.frame.IFrame;
import com.ttProject.frame.IVideoFrame;

/**
 * flvの書き込み動作
 * @author taktod
 */
public class FlvTagWriter implements IWriter {
	/** ロガー */
	private Logger logger = Logger.getLogger(FlvTagWriter.class);
	private final WritableByteChannel outputChannel;
	private FileOutputStream outputStream = null;
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
		logger.info("フレームを受け取りました:" + frame);
		if(frame instanceof IAudioFrame) {
			// 音声の書き込み
			AudioTag aTag = new AudioTag();
			aTag.setFrame((IAudioFrame)frame);
//			outputChannel.write(aTag.getData());
		}
		else if(frame instanceof IVideoFrame) {
			// 映像の書き込み
			VideoTag vTag = new VideoTag();
			vTag.setFrame((IVideoFrame)frame);
			outputChannel.write(vTag.getData());
		}
	}
	@Override
	public void prepareHeader() throws Exception {
		logger.info("headerを準備します。");
	}
	@Override
	public void prepareTailer() throws Exception {
		logger.info("tailerを準備します。");
		if(outputStream != null) {
			try {
				outputStream.close();
			}
			catch(Exception e) {}
			outputStream = null;
		}
	}
}
