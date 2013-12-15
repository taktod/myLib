package com.ttProject.frame.extra;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.ttProject.frame.IVideoFrame;
import com.ttProject.nio.channels.IReadChannel;

/**
 * 映像のframeを同時に複数もつframe
 * flvのvideoTagのh264とかで利用します。
 * @author taktod
 */
public class VideoMultiFrame implements IVideoFrame {
	/** 保持フレーム */
	private List<IVideoFrame> frameList = new ArrayList<IVideoFrame>();
	@Override
	public long getPts() {
		return 0;
	}
	@Override
	public long getTimebase() {
		return 0;
	}
	@Override
	public ByteBuffer getData() throws Exception {
		return null;
	}
	@Override
	public int getSize() {
		return 0;
	}
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
	}
	@Override
	public void load(IReadChannel channel) throws Exception {
	}
	@Override
	public long getDts() {
		return 0;
	}
	@Override
	public int getWidth() {
		return 0;
	}
	@Override
	public int getHeight() {
		return 0;
	}
}
