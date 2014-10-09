package com.ttProject.container.riff.type;

import com.ttProject.container.riff.RiffMasterUnit;
import com.ttProject.container.riff.Type;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.util.BufferUtil;

/**
 * riff
 * @author taktod
 */
public class Riff extends RiffMasterUnit {
	private String formatString;
	/**
	 * constructor
	 */
	public Riff() {
		super(Type.RIFF);
	}
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		super.minimumLoad(channel);
		formatString = new String(BufferUtil.safeRead(channel, 4).array()).intern();
	}
	@Override
	public void load(IReadChannel channel) throws Exception {
	}
	@Override
	protected void requestUpdate() throws Exception {
	}
	public String getFormatString() {
		return formatString;
	}
}
