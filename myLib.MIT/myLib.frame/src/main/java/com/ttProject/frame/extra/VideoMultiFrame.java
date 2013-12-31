package com.ttProject.frame.extra;

import java.nio.ByteBuffer;
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
	/**
	 * フレームを追加します
	 * @param frame
	 * @throws Exception
	 */
	public void addFrame(IVideoFrame frame) throws Exception {
		setPts(frame.getPts());
		setTimebase(frame.getTimebase());
		setWidth(frame.getWidth());
		setHeight(frame.getHeight());
		if(frameList.size() == 0) {
			if(frame.isKeyFrame()) {
				setKeyFrame(true);
			}
			setSize(frame.getSize());
		}
		else {
			// キーフレームを取得したら、キーフレームの内容でいったん初期化しておく。
			if(frame.isKeyFrame()) {
				setKeyFrame(true);
			}
			setSize(frame.getSize() + getSize());
		}
		frameList.add(frame);
	}
	/**
	 * {@inheritDoc}
	 */
	@Deprecated
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
	}
	/**
	 * {@inheritDoc}
	 */
	@Deprecated
	@Override
	public void load(IReadChannel channel) throws Exception {
	}
	/**
	 * {@inheritDoc}
	 */
	@Deprecated
	@Override
	protected void requestUpdate() throws Exception {
	}
	/**
	 * {@inheritDoc}
	 */
	@Deprecated
	@Override
	public float getDuration() {
		return 0;
	}
	/**
	 * {@inheritDoc}
	 */
	@Deprecated
	@Override
	public ByteBuffer getPackBuffer() {
		return null;
	}
	/**
	 * frameリスト参照
	 * @return
	 */
	public List<IVideoFrame> getFrameList() {
		return new ArrayList<IVideoFrame>(frameList);
	}
}
