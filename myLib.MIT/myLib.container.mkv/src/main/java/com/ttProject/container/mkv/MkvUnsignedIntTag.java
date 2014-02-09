package com.ttProject.container.mkv;

import java.util.ArrayList;
import java.util.List;

import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.BitN;
import com.ttProject.unit.extra.EbmlValue;
import com.ttProject.unit.extra.bit.Bit8;

/**
 * unsignedIntを保持しているtagの動作
 * @author taktod
 */
public abstract class MkvUnsignedIntTag extends MkvTag {
	private BitN value;
	/**
	 * コンストラクタ
	 * @param id
	 * @param size
	 */
	public MkvUnsignedIntTag(Type id, EbmlValue size) {
		super(id, size);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(IReadChannel channel) throws Exception {
		List<Bit8> bit8List = new ArrayList<Bit8>();
		for(int i = 0;i < getMkvSize();i ++) {
			bit8List.add(new Bit8());
		}
		BitLoader loader = new BitLoader(channel);
		loader.load(bit8List.toArray(new Bit8[]{}));
		value = new BitN(bit8List.toArray(new Bit8[]{}));
		super.load(channel);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString(String space) {
		StringBuilder data = new StringBuilder();
		data.append(super.toString(space));
		data.append(" uint:").append(value.getLong());
		return data.toString();
	}
}
