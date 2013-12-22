package com.ttProject.container.adts;

import com.ttProject.container.Container;
import com.ttProject.frame.IAudioFrame;
import com.ttProject.frame.aac.AacFrame;
import com.ttProject.nio.channels.IReadChannel;

/**
 * adtsのファイルユニット
 * @author taktod
 */
public class AdtsUnit extends Container {
	private final AacFrame frame;
	/**
	 * コンストラクタ
	 * @param frame
	 * @param position
	 * @param pts
	 */
	public AdtsUnit(AacFrame frame, int position, long pts) {
		this.frame = frame;
		setPosition(position);
		setPts(pts);
		setSize(frame.getSize());
		setTimebase(frame.getSampleRate());
		frame.setPts(pts);
		frame.setTimebase(frame.getSampleRate());
	}
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
	}

	@Override
	public void load(IReadChannel channel) throws Exception {
		frame.load(channel);
	}

	@Override
	protected void requestUpdate() throws Exception {
		setData(frame.getData());
	}
	public IAudioFrame getFrame() throws Exception {
		return frame;
	}
}
