package com.ttProject.container.mkv;

import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.BitN;
import com.ttProject.unit.extra.EbmlValue;
import com.ttProject.unit.extra.bit.Bit32;
import com.ttProject.unit.extra.bit.Bit64;

/**
 * floatを保持しているtagの動作(doubleもあるらしい)
 * @author taktod
 */
public abstract class MkvFloatTag extends MkvTag {
	private BitN value;
	/**
	 * コンストラクタ
	 * @param id
	 * @param size
	 */
	public MkvFloatTag(Type id, EbmlValue size) {
		super(id, size);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(IReadChannel channel) throws Exception {
		switch(getMkvSize()) {
		case 4:
			value = new Bit32();
			break;
		case 8:
			value = new Bit64();
			break;
		default:
			throw new Exception("4,8以外の数値では、動作できません。");
		}
		BitLoader loader = new BitLoader(channel);
		loader.load(value);
		super.load(channel);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder data = new StringBuilder();
		data.append("class:").append(getClass().getSimpleName());
		data.append(" size:").append(Integer.toHexString(getMkvSize()));
		if(value instanceof Bit32) {
			data.append(" float:").append(Float.intBitsToFloat(value.get()));
		}
		else if(value instanceof Bit64) {
			data.append(" float:").append(Double.longBitsToDouble(value.getLong()));
		}
		else {
			throw new RuntimeException("想定外のデータを保持しています。:" + value);
		}
		return data.toString();
	}
}
