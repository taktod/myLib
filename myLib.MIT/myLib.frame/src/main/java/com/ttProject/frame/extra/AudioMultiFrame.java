package com.ttProject.frame.extra;

import java.nio.ByteBuffer;

import com.ttProject.frame.IAudioFrame;
import com.ttProject.nio.channels.IReadChannel;

/**
 * audioFrameを複数同時に持つ場合のframe
 * flvのaudioTagのnellymoserとかで利用します。(nellymoserでは、1,2,4ユニットが混じった動作とかあるので)
 * @author taktod
 */
public class AudioMultiFrame implements IAudioFrame {
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
	public int getSampleNum() {
		return 0;
	}
	@Override
	public int getSampleRate() {
		return 0;
	}
	@Override
	public int getChannel() {
		return 0;
	}
	@Override
	public int getBit() {
		return 0;
	}
}
