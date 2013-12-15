package com.ttProject.frame.aac;

import org.apache.log4j.Logger;

import com.ttProject.frame.aac.type.Frame;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.ISelector;
import com.ttProject.unit.IUnit;

/**
 * dsiベースのaacのframeを選択する動作
 * @author taktod
 */
public class AacDsiFrameSelector implements ISelector {
	/** ロガー */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(AacDsiFrameSelector.class);
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
		Frame frame = new Frame();
		frame.loadDecoderSpecificInfo(channel.size(), dsi, channel);
		return frame;
	}
}
