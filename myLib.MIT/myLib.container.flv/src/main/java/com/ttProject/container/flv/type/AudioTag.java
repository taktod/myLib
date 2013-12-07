package com.ttProject.container.flv.type;

import com.ttProject.container.flv.FlvTag;
import com.ttProject.unit.extra.Bit1;
import com.ttProject.unit.extra.Bit2;
import com.ttProject.unit.extra.Bit4;
import com.ttProject.unit.extra.Bit8;

/**
 * 音声用のtag
 * @author taktod
 *
 */
public class AudioTag extends FlvTag {
	private Bit4 codecId;
	private Bit2 sampleRate;
	private Bit1 bitCount;
	private Bit1 channels;
	private Bit8 sequenceHeaderFlag;
}
