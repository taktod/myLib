package com.ttProject.frame.opus.type;

import java.nio.ByteBuffer;

import com.ttProject.frame.opus.OpusFrame;
import com.ttProject.nio.channels.IReadChannel;

/**
 * opusのフレーム
 * @author taktod
 *
 */
public class Frame extends OpusFrame {

	@Override
	public ByteBuffer getPackBuffer() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void load(IReadChannel channel) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isComplete() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void requestUpdate() throws Exception {
		// TODO Auto-generated method stub
		
	}
	
}
