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
import com.ttProject.frame.aac.DecoderSpecificInfo;
import com.ttProject.frame.h264.type.PictureParameterSet;
import com.ttProject.frame.h264.type.SequenceParameterSet;

/**
 * flvの書き込み動作
 * @author taktod
 */
public class FlvTagWriter implements IWriter {
	/** ロガー */
	private Logger logger = Logger.getLogger(FlvTagWriter.class);
	private final WritableByteChannel outputChannel;
	private FileOutputStream outputStream = null;
	
	// aacのmsh用のdsiデータ
	private DecoderSpecificInfo  dsi = null;
	// h264のmsh用のspsとppsデータ
	private SequenceParameterSet sps = null;
	private PictureParameterSet  pps = null;
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
			IAudioFrame audioFrame = (IAudioFrame) frame;
			// aacの場合はmshをつくらないとだめ
			checkAacMshTag(audioFrame);
			// 音声の書き込み
			AudioTag aTag = new AudioTag();
			aTag.setFrame(audioFrame);
//			outputChannel.write(aTag.getData());
		}
		else if(frame instanceof IVideoFrame) {
			IVideoFrame videoFrame = (IVideoFrame) frame;
			// h264の場合はmshをつくらないとだめ
			checkH264MshTag(videoFrame);
			// 映像の書き込み
			VideoTag vTag = new VideoTag();
			vTag.setFrame(videoFrame);
//			outputChannel.write(vTag.getData());
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
	private void checkAacMshTag(IAudioFrame audioFrame) {
		
	}
	private void checkH264MshTag(IVideoFrame videoFrame) {
		
	}
}
