package com.ttProject.container.riff.type;

import com.ttProject.container.riff.RiffUnit;
import com.ttProject.container.riff.Type;
import com.ttProject.nio.channels.IReadChannel;

/**
 * hdrl
 * @author taktod
 * this riff unit doesn't have size information.
 */
public class Hdrl extends RiffUnit {
	/**
	 * constructor
	 */
	public Hdrl() {
		super(Type.hdrl);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(IReadChannel channel) throws Exception {
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
	}
}
