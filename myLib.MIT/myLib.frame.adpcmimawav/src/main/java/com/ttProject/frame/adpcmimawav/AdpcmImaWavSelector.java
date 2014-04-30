package com.ttProject.frame.adpcmimawav;

import com.ttProject.frame.AudioSelector;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.IUnit;

/**
 * 
 * @author taktod
 */
public class AdpcmImaWavSelector extends AudioSelector {
	@Override
	public IUnit select(IReadChannel channel) throws Exception {
		throw new Exception("データ作成が未実装");
	}
}
