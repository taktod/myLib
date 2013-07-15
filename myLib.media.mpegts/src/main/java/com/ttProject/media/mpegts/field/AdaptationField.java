package com.ttProject.media.mpegts.field;

import com.ttProject.media.extra.Bit;
import com.ttProject.media.extra.Bit1;
import com.ttProject.media.extra.Bit8;
import com.ttProject.nio.channels.IReadChannel;

/**
 * adaptationFieldの内容保持
 * @author taktod
 */
public class AdaptationField {
	private Bit8 adaptationFieldLength;
	private Bit1 discontinuityIndicator;
	private Bit1 randomAccessIndicator;
	private Bit1 elementaryStreamPriorityIndicator;
	private Bit1 pcrFlag;
	private Bit1 opcrFlag;
	private Bit1 splicingPointFlag;
	private Bit1 transportPrivateDataFlag;
	private Bit1 adaptationFieldExtensionFlag;
	// pcr opcr spliceCountdown stuffingBytes等々・・・
	public void analyze(IReadChannel channel) throws Exception {
		// とりあえずlengthをみておく。
		adaptationFieldLength = new Bit8();
		Bit.bitLoader(channel, adaptationFieldLength);
		if(adaptationFieldLength.get() == 0x00) {
			// データがなければここでおわり。
			return;
		}
		// 他のデータがある場合は読み込んでいく必要あり。
		throw new Exception("未解析はadaptationFieldがありました。");
	}
}
