package com.ttProject.container.riff.type;

import com.ttProject.container.riff.RiffSizeUnit;
import com.ttProject.container.riff.Type;
import com.ttProject.nio.channels.IReadChannel;

/**
 * strf
 * @author taktod
 */
public class Strf extends RiffSizeUnit {
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
}

