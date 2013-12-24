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
		if(frameList.size() == 0) {
			setPts(frame.getPts());
			setTimebase(frame.getTimebase());
			setWidth(frame.getWidth());
			setHeight(frame.getHeight());
		}
		else {
			// とりあえずデータの不一致についてはいまは目をつむっておく。
//			if(frame.getWidth() != getWidth() || frame.getHeight() != getHeight()) {
//				throw new Exception("値の違うframeが同じデータとして追加されました。");
//			}
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
