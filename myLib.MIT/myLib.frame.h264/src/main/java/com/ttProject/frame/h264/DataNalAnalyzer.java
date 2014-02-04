package com.ttProject.frame.h264;

import org.apache.log4j.Logger;

import com.ttProject.frame.IFrame;
import com.ttProject.frame.VideoAnalyzer;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.util.BufferUtil;

/**
 * flvやh264の様なsize + dataのnalを解析する動作
 * 実体の読み込みまで実施します。
 * @author taktod
 */
public class DataNalAnalyzer extends VideoAnalyzer {
	/** ロガー */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(DataNalAnalyzer.class);
	/** 現在処理フレーム */
	private H264Frame h264Frame = null;
	/**
	 * コンストラクタ
	 */
	public DataNalAnalyzer() {
		super(new H264FrameSelector());
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public IFrame analyze(IReadChannel channel) throws Exception {
		if(channel.size() < 4) {
			throw new Exception("読み込みバッファ量がおかしいです。");
		}
		int size = BufferUtil.safeRead(channel, 4).getInt();
		if(size <= 0) {
			throw new Exception("データ指定がおかしいです。");
		}
		if(channel.size() - channel.position() < size) {
			throw new Exception("データが足りません");
		}
		IReadChannel byteChannel = new ByteReadChannel(BufferUtil.safeRead(channel, size));
		H264Frame frame = (H264Frame)getSelector().select(byteChannel);
		frame.load(byteChannel);
		if(h264Frame == null || h264Frame.getClass() != frame.getClass() || (frame instanceof SliceFrame && ((SliceFrame)frame).getFirstMbInSlice() == 0)) {
			logger.info("新規データみたいです。");
			h264Frame = frame;
		}
		else {
			logger.info("追記データみたいです。");
		}
		h264Frame.addFrame(frame);
		return frame;
	}
}
