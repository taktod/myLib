package com.ttProject.frame.h265;

import org.apache.log4j.Logger;

import com.ttProject.frame.IFrame;
import com.ttProject.frame.NullFrame;
import com.ttProject.frame.VideoAnalyzer;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.util.BufferUtil;

/**
 * mkvやmp4等のh265のsize + dataのnalを解析する動作
 * @author taktod
 */
public class H265DataNalAnalyzer extends VideoAnalyzer {
	/** ロガー */
	private Logger logger = Logger.getLogger(H265DataNalAnalyzer.class);
	/** 現在処理中のフレーム */
	private H265Frame h265Frame = null;
	/**
	 * コンストラクタ
	 */
	public H265DataNalAnalyzer() {
		super(new H265FrameSelector());
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
		H265Frame frame = (H265Frame) getSelector().select(byteChannel);
		frame.load(byteChannel);
		if(h265Frame == null || h265Frame.getClass() != frame.getClass()) {
			// 1つ前のデータを応答するので保持しておく。
			IFrame oldFrame = h265Frame;
			if(oldFrame == null) {
				oldFrame = NullFrame.getInstance();
			}
			h265Frame = frame;
			h265Frame.addFrame(frame);
			return oldFrame;
		}
		else {
			h265Frame.addFrame(frame);
			return NullFrame.getInstance();
		}
	}
	@Override
	public IFrame getRemainFrame() throws Exception {
		H265Frame frame = h265Frame;
		h265Frame = null;
		return frame;
	}
}
