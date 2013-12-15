package com.ttProject.frame.aac;

import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.ISelector;
import com.ttProject.unit.IUnit;

/**
 * dsiベースのaacのframeを選択する動作
 * @author taktod
 */
public class AacDsiFrameSelector implements ISelector {
	/** 処理のdsi */
	private DecoderSpecificInfo dsi = null;
	/**
	 * dsiのsetter
	 * @param dsi
	 */
	public void setDecoderSpecificInfo(DecoderSpecificInfo dsi) {
		this.dsi = dsi;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public IUnit select(IReadChannel channel) throws Exception {
		if(dsi == null) {
			throw new Exception("dsiが未定義なので処理を進めることができません");
		}
		return null;
	}
}
