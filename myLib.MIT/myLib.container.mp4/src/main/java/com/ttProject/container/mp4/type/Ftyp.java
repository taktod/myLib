package com.ttProject.container.mp4.type;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ttProject.container.mp4.Mp4Atom;
import com.ttProject.container.mp4.Type;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit32;
import com.ttProject.util.IntUtil;

/**
 * ftypの定義
 * @author taktod
 * 
 * とりあえずloadを実行したら読み込むか、minimumLoadで読み込むか迷うところ
 */
public class Ftyp extends Mp4Atom {
	/** ロガー */
	private Logger logger = Logger.getLogger(Ftyp.class);
	private Bit32 majorBrand = new Bit32();
	private Bit32 minorVersion = new Bit32();
	private List<Bit32> compatibleBrands = new ArrayList<Bit32>();
	/**
	 * コンストラクタ
	 * @param size
	 * @param name
	 */
	public Ftyp(Bit32 size, Bit32 name) {
		super(size, name);
	}
	/**
	 * コンストラクタ
	 */
	public Ftyp() {
		super(new Bit32(), Type.getTypeBit(Type.Ftyp));
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		super.minimumLoad(channel);
		// campatibleBrandの数を計算しておく。
		int numOfCompatibleBrand = (getSize() - 16) / 4;
		logger.info("campatibleBrandの数:" + numOfCompatibleBrand);
		BitLoader loader = new BitLoader(channel);
		loader.load(majorBrand, minorVersion);
		logger.info("majorBrand:" + IntUtil.makeHexString(majorBrand.get()));
		for(int i = 0;i < numOfCompatibleBrand;i ++) {
			Bit32 brand = new Bit32();
			loader.load(brand);
			compatibleBrands.add(brand);
			logger.info("compatibleBrand:" + IntUtil.makeHexString(brand.get()));
		}
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
	}
}
