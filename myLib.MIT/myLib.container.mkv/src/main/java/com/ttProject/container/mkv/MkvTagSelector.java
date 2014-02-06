package com.ttProject.container.mkv;

import org.apache.log4j.Logger;

import com.ttProject.container.mkv.type.DocType;
import com.ttProject.container.mkv.type.DocTypeReadVersion;
import com.ttProject.container.mkv.type.DocTypeVersion;
import com.ttProject.container.mkv.type.EBML;
import com.ttProject.container.mkv.type.EBMLMaxIDLength;
import com.ttProject.container.mkv.type.EBMLMaxSizeLength;
import com.ttProject.container.mkv.type.EBMLReadVersion;
import com.ttProject.container.mkv.type.EBMLVersion;
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
		case EBMLVersion:
			mkvTag = new EBMLVersion(size);
			break;
		case EBMLReadVersion:
			mkvTag = new EBMLReadVersion(size);
			break;
		case EBMLMaxIDLength:
			mkvTag = new EBMLMaxIDLength(size);
			break;
		case EBMLMaxSizeLength:
			mkvTag = new EBMLMaxSizeLength(size);
			break;
		case DocType:
			mkvTag = new DocType(size);
			break;
		case DocTypeVersion:
			mkvTag = new DocTypeVersion(size);
			break;
		case DocTypeReadVersion:
			mkvTag = new DocTypeReadVersion(size);
			break;
		default:
			throw new Exception("未実装のTypeデータが応答されました。" + Type.getType(tag.getEbmlValue()));
		}
		mkvTag.minimumLoad(channel);
		return mkvTag;
	}
}
