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
	 * @throws Exception
	 */
	public void addFrame(IAudioFrame frame) throws Exception {
		if(frameList.size() == 0) {
			setBit(frame.getBit());
			setChannel(frame.getChannel());
			setPts(frame.getPts());
			setTimebase(frame.getTimebase());
			setSampleRate(getSampleRate());
			setSampleNum(frame.getSampleNum());
		}
		else {
			// データの不一致はいまのところほっとく。
			setSampleNum(frame.getSampleNum() + getSampleNum()); // サンプル数は足していく。
		}
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
