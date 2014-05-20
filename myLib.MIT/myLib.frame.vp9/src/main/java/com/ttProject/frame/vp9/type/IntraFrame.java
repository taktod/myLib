package com.ttProject.frame.vp9.type;

import java.nio.ByteBuffer;

import com.ttProject.frame.vp9.Vp9Frame;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit2;

public class IntraFrame extends Vp9Frame {
	public IntraFrame(Bit2 frameMarker, Bit1 profile, Bit1 reserved, Bit1 refFlag,
			Bit1 keyFrameFlag, Bit1 invisibleFlag, Bit1 errorRes) {
		super(frameMarker, profile, reserved, refFlag, keyFrameFlag, invisibleFlag, errorRes);
	}

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
	protected void requestUpdate() throws Exception {
		// TODO Auto-generated method stub
		
	}
	
}

