package com.ttProject.container.riff.type;

import com.ttProject.container.Container;
import com.ttProject.nio.channels.IReadChannel;

/**
 * hdrl
 * @author taktod
 * this riff unit doesn't have size information.
 */
public class Hdrl extends Container {
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
