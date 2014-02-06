package com.ttProject.container.mkv;

import org.apache.log4j.Logger;

import com.ttProject.container.mkv.type.EBML;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.ISelector;
import com.ttProject.unit.IUnit;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.EbmlValue;

/**
 * mkvのelementを解析して取り出すselector
 * @author taktod
 */
public class MkvTagSelector implements ISelector {
	/** ロガー */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(MkvTagSelector.class);
	/**
	 * {@inheritDoc}
	 */
	@Override
	public IUnit select(IReadChannel channel) throws Exception {
		if(channel.position() == channel.size()) {
			// データがもうない
			return null;
		}
		EbmlValue tag  = new EbmlValue();
		EbmlValue size = new EbmlValue();
		BitLoader loader = new BitLoader(channel);
		loader.load(tag, size);
		MkvTag mkvTag = null;
		switch(Type.getType(tag.getEbmlValue())) {
		case EBML:
			mkvTag = new EBML(size);
			break;
		default:
			throw new Exception("未実装のTypeデータが応答されました。" + Type.getType(tag.getEbmlValue()));
		}
		mkvTag.minimumLoad(channel);
		return mkvTag;
	}
}
