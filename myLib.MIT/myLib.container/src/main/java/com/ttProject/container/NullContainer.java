package com.ttProject.container;

import com.ttProject.nio.channels.IReadChannel;

/**
 * 空のコンテナ
 * @author taktod
 *
 */
public class NullContainer extends Container {
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
