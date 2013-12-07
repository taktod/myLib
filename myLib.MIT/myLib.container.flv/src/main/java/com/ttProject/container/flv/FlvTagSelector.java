package com.ttProject.container.flv;

import org.apache.log4j.Logger;

import com.ttProject.container.flv.type.AudioTag;
import com.ttProject.container.flv.type.MetaTag;
import com.ttProject.container.flv.type.VideoTag;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.ISelector;
import com.ttProject.unit.IUnit;
import com.ttProject.unit.extra.Bit8;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.BitN.Bit16;
import com.ttProject.unit.extra.BitN.Bit24;

/**
 * flvのtagを解析して取り出すselector
 * @author taktod
 */
public class FlvTagSelector implements ISelector {
	/** ロガー */
	private Logger logger = Logger.getLogger(FlvTagSelector.class);
	/**
	 * {@inheritDoc}
	 */
	@Override
	public IUnit select(IReadChannel channel) throws Exception {
		// 始めの8bitを見ればなんのデータか一応わかる。
		Bit8 firstByte = new Bit8();
		BitLoader loader = new BitLoader(channel);
		loader.load(firstByte);
		IUnit unit;
		if(firstByte.get() == 'F') {
			// headerデータ
			Bit16 restSignature = new Bit16();
			loader.load(restSignature);
			Bit24 signature = new Bit24(firstByte.get() << 16 | restSignature.get());
			unit = new FlvHeaderTag(signature);
			unit.minimumLoad(channel);
			return unit;
		}
		else {
			// その他のtagであると思われる。
			logger.info("別のタグ" + firstByte.get());
			switch(firstByte.get()) {
			case 0x12:
				unit = new MetaTag(firstByte);
				break;
			case 0x09:
				unit = new VideoTag(firstByte);
				break;
			case 0x08:
				unit = new AudioTag(firstByte);
				break;
			default:
				throw new Exception("想定外のtagです。");
			}
			unit.minimumLoad(channel);
			return unit;
		}
	}
}
