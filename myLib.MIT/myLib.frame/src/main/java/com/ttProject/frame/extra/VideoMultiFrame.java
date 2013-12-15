package com.ttProject.frame.extra;

import java.util.ArrayList;
import java.util.List;

import com.ttProject.frame.IVideoFrame;
import com.ttProject.frame.VideoFrame;
import com.ttProject.nio.channels.IReadChannel;

/**
 * 映像のframeを同時に複数もつframe
 * flvのvideoTagのh264とかで利用します。
 * @author taktod
 */
public class VideoMultiFrame extends VideoFrame {
	/** 保持フレーム */
	private List<IVideoFrame> frameList = new ArrayList<IVideoFrame>();
	public void addFrame(IVideoFrame frame) {
		frameList.add(frame);
	}
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
	}
	@Override
	public void load(IReadChannel channel) throws Exception {
	}
	@Override
	protected void requestUpdate() throws Exception {
		
	}
}
