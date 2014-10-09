package com.ttProject.container.riff.type;

import com.ttProject.container.riff.RiffFormatUnit;
import com.ttProject.container.riff.Type;
import com.ttProject.frame.CodecType;
import com.ttProject.frame.IAnalyzer;
import com.ttProject.nio.channels.IReadChannel;

/**
 * strf
 * @author taktod
 */
public class Strf extends RiffFormatUnit {
	/**
	 * constructor
	 */
	public Strf() {
		super(Type.strf);
	}
	@Override
	public void load(IReadChannel channel) throws Exception {
		
	}
	@Override
	protected void requestUpdate() throws Exception {
		
	}
	@Override
	public CodecType getCodecType() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public IAnalyzer getFrameAnalyzer() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public int getBlockSize() {
		// TODO Auto-generated method stub
		return 0;
	}
}

