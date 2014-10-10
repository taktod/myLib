package com.ttProject.container.riff.type;

import com.ttProject.container.riff.RiffUnit;
import com.ttProject.container.riff.Type;
import com.ttProject.nio.channels.IReadChannel;

/**
 * Movi
 * @author taktod
 */
public class Movi extends RiffUnit {
	/**
	 * constructor
	 */
	public Movi() {
		super(Type.movi);
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
