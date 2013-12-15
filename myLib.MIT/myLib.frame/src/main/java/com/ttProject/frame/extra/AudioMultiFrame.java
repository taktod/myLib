package com.ttProject.frame.extra;

import java.util.ArrayList;
import java.util.List;

import com.ttProject.frame.AudioFrame;
import com.ttProject.frame.IAudioFrame;
import com.ttProject.nio.channels.IReadChannel;

/**
 * audioFrameを複数同時に持つ場合のframe
 * flvのaudioTagのnellymoserとかで利用します。(nellymoserでは、1,2,4ユニットが混じった動作とかあるので)
 * @author taktod
 */
public class AudioMultiFrame extends AudioFrame {
	private List<IAudioFrame> frameList = new ArrayList<IAudioFrame>();
	/**
	 * フレームを追加します
	 * @param frame
	 */
	public void addFrame(IAudioFrame frame) {
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
