package com.ttProject.container.mp3;

import com.ttProject.container.Container;
import com.ttProject.frame.IAudioFrame;
import com.ttProject.frame.mp3.Mp3Frame;
import com.ttProject.nio.channels.IReadChannel;

/**
 * mp3のファイルユニット
 * @author taktod
 *
 */
public class Mp3Unit extends Container {
	private final Mp3Frame frame;
	/**
	 * コンストラクタ
	 * @param frame
	 * @param position
	 * @param pts
	 */
	public Mp3Unit(Mp3Frame frame, int position, long pts) {
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
