package com.ttProject.container.adts;

import java.io.FileOutputStream;
import java.nio.channels.WritableByteChannel;

import org.apache.log4j.Logger;

import com.ttProject.container.IContainer;
import com.ttProject.container.IWriter;
import com.ttProject.frame.IFrame;
import com.ttProject.frame.aac.type.Frame;

public class AdtsUnitWriter implements IWriter {
	/** ロガー */
	private Logger logger = Logger.getLogger(AdtsUnitWriter.class);
	private final WritableByteChannel outputChannel;
	private FileOutputStream outputStream = null;
	/**
	 * コンストラクタ
	 * @param fileName
	 */
	public AdtsUnitWriter(String fileName) throws Exception {
		outputStream = new FileOutputStream(fileName);
		this.outputChannel = outputStream.getChannel();
	}
	public AdtsUnitWriter(FileOutputStream fileOutputStream) {
		this.outputChannel = fileOutputStream.getChannel();
	}
	public AdtsUnitWriter(WritableByteChannel outputChannel) {
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
	public void prepareHeader() throws Exception {
		logger.info("header準備");
	}

	@Override
	public void prepareTailer() throws Exception {
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
