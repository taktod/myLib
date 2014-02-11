package com.ttProject.container.mkv.type;

import com.ttProject.container.mkv.MkvMasterTag;
import com.ttProject.container.mkv.Type;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.EbmlValue;

/**
 * Clusterタグ
 * @author taktod
 */
public class Cluster extends MkvMasterTag {
	/**
	 * コンストラクタ
	 * @param size
	 */
	public Cluster(EbmlValue size) {
		super(Type.Cluster, size);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
	}
	@Override
	public void load(IReadChannel channel) throws Exception {
		super.load(channel);
	}
}
