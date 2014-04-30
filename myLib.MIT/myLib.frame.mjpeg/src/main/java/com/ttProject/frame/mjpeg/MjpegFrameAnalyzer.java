package com.ttProject.frame.mjpeg;

import com.ttProject.frame.VideoAnalyzer;

/**
 * mjpegのframe解析
 * @author taktod
 */
public class MjpegFrameAnalyzer extends VideoAnalyzer {
	/**
	 * コンストラクタ
	 */
	public MjpegFrameAnalyzer() {
		super(new MjpegFrameSelector());
	}
}
