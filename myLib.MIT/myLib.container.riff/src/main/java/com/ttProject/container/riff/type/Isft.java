package com.ttProject.container.riff.type;

import com.ttProject.container.riff.RiffSizeUnit;
import com.ttProject.container.riff.Type;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.util.BufferUtil;

/**
 * Isft
 * software information?
 * @author taktod
 */
public class Isft extends RiffSizeUnit {
	private String softwareInfo;
	/**
	 * constructor
	 */
	public Isft() {
		super(Type.ISFT);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		super.minimumLoad(channel);
		softwareInfo = new String(BufferUtil.safeRead(channel, getSize() - 8).array()).intern();
	}
	@Override
	public void load(IReadChannel channel) throws Exception {
		
	}
	@Override
	protected void requestUpdate() throws Exception {
		
	}
}
