package com.ttProject.container.mkv;

import org.apache.log4j.Logger;

import com.ttProject.container.IContainer;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.EbmlValue;

/**
 * 他のTagを内包するTagの動作
 * @author taktod
 */
public abstract class MkvMasterTag extends MkvTag {
	/** ロガー */
	private Logger logger = Logger.getLogger(MkvMasterTag.class);
	/**
	 * コンストラクタ
	 * @param tag
	 * @param size
	 */
	public MkvMasterTag(Type id, EbmlValue size) {
		super(id, size);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(IReadChannel channel) throws Exception {
		int targetSize = getMkvSize();
		IContainer container = null;
		while(targetSize > 0 && (container = getMkvTagReader().read(channel)) != null) {
			targetSize -= container.getSize();
			logger.info(container);
		}
		super.load(channel);
	}
}
