package com.ttProject.container.riff.type;

import com.ttProject.container.riff.RiffUnit;
import com.ttProject.container.riff.Type;
import com.ttProject.nio.channels.IReadChannel;

/**
 * Info
 * ??
 * @author taktod
 */
public class Info extends RiffUnit {
	/**
	 * constructor
	 */
	public Info() {
		super(Type.INFO);
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
