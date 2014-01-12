package com.ttProject.container.flv;

import java.io.FileOutputStream;
import java.nio.channels.WritableByteChannel;
import java.util.List;

import org.apache.log4j.Logger;

import com.ttProject.container.IContainer;
import com.ttProject.container.IWriter;
import com.ttProject.frame.IFrame;
import com.ttProject.util.HexUtil;

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
		logger.info("フレームを受け取りました:" + frame);
		List<FlvTag> tagList = frameConverter.getTags(frame);
		if(tagList != null) {
			for(FlvTag tag : tagList) {
//				logger.info(HexUtil.toHex(tag.getData(), true));
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
		// h264の場合はend tagをいれた方がよい。
		if(outputStream != null) {
			try {
				outputStream.close();
			}
			catch(Exception e) {}
			outputStream = null;
		}
	}
}
