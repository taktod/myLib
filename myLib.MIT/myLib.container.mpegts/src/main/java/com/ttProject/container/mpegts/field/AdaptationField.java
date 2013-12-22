package com.ttProject.container.mpegts.field;

import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit33;
import com.ttProject.unit.extra.bit.Bit6;
import com.ttProject.unit.extra.bit.Bit8;
import com.ttProject.unit.extra.bit.Bit9;

/**
 * adaptationFieldの内容保持
 * @author taktod
 */
public class AdaptationField {
	private Bit8 adaptationFieldLength;
	private Bit1 discontinuityIndicator; // 0
	private Bit1 randomAccessIndicator; // aacの先頭だけ、たってる？ (aacのみでも同様)(h264のキーフレームもたってるっぽい)
	private Bit1 elementaryStreamPriorityIndicator; // 0
	private Bit1 pcrFlag;
	private Bit1 opcrFlag; // originalPcr(コピーするときにつかうらしい。) // 0
	private Bit1 splicingPointFlag; // 0
	private Bit1 transportPrivateDataFlag; // 0
	private Bit1 adaptationFieldExtensionFlag; // 0
	// pcr
	private Bit33 pcrBase;
	private Bit6 pcrPadding;
	private Bit9 pcrExtension;
	// opcr
	private Bit33 opcrBase;
	private Bit6 opcrPadding;
	private Bit9 opcrExtension;
}
