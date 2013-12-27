package com.ttProject.container.mp3;

import java.io.FileOutputStream;
import java.nio.channels.WritableByteChannel;

import org.apache.log4j.Logger;

import com.ttProject.container.IContainer;
import com.ttProject.container.IWriter;
import com.ttProject.frame.IFrame;
import com.ttProject.frame.mp3.type.Frame;

/**
 * mp3の書き込み動作
 * @author taktod
 */
public class Mp3UnitWriter implements IWriter {
	/** ロガー */
	private Logger logger = Logger.getLogger(Mp3UnitWriter.class);
	private final WritableByteChannel outputChannel;
	private FileOutputStream outputStream = null;
	/**
	 * コンストラクタ
	 * @param fileName
	 */
	public Mp3UnitWriter(String fileName) throws Exception {
		outputStream = new FileOutputStream(fileName);
		this.outputChannel = outputStream.getChannel();
	}
	public Mp3UnitWriter(FileOutputStream fileOutputStream) {
		this.outputChannel = fileOutputStream.getChannel();
	}
	public Mp3UnitWriter(WritableByteChannel outputChannel) {
		this.outputChannel = outputChannel;
	}
	@Override
	public void addContainer(IContainer container) throws Exception {
	}
	@Override
	public void addFrame(int trackId, IFrame frame) throws Exception {
		if(frame instanceof Frame) {
			logger.info("frameを書き込む" + frame.toString());
			outputChannel.write(frame.getData());
		}
	}
	@Override
	public void prepareHeader() {
		logger.info("header準備");
	}
	@Override
	public void prepareTailer() {
		logger.info("tailer準備");
		if(outputStream != null) {
			try {
				outputStream.close();
			}
			catch(Exception e) {}
			outputStream = null;
		}
	}
}
