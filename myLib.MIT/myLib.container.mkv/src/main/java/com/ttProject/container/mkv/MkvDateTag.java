package com.ttProject.container.mkv;

import java.util.Date;

import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.EbmlValue;
import com.ttProject.unit.extra.bit.Bit64;

/**
 * dateを保持しているtagの動作
 * 2001/01/01からの経過ナノ秒らしい
 * @author taktod
 */
public abstract class MkvDateTag extends MkvTag {
	private Bit64 value;
	/**
	 * コンストラクタ
	 * @param id
	 * @param size
	 */
	public MkvDateTag(Type id, EbmlValue size) {
		super(id, size);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(IReadChannel channel) throws Exception {
		switch(getMkvSize()) {
		case 8:
			value = new Bit64();
			break;
		default:
			throw new Exception("8以外の数値では、動作できません。");
		}
		BitLoader loader = new BitLoader(channel);
		loader.load(value);
		super.load(channel);
	}
	public Date getValue() {
		return new Date(946684800000L + value.getLong() / 1000000);
//		return value.getLong();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString(String space) {
		StringBuilder data = new StringBuilder();
		data.append(super.toString(space));
		data.append(" date:").append(getValue());
		return data.toString();
	}
}
