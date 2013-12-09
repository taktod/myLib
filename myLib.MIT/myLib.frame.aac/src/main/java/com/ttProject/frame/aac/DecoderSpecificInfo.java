package com.ttProject.frame.aac;

import com.ttProject.unit.extra.Bit1;
import com.ttProject.unit.extra.Bit4;
import com.ttProject.unit.extra.Bit5;
import com.ttProject.unit.extra.Bit6;
import com.ttProject.unit.extra.BitN.Bit24;

/**
 * aacのdecode specific infoのデータから
 * @see http://wiki.multimedia.cx/index.php?title=MPEG-4_Audio
 * これ・・・扱い的にはglobalHeaderらしい
 * @author taktod
 */
public class DecoderSpecificInfo {
	private Bit5 objectType1 = new Bit5(); // profileの事
	private Bit6 objectType2 = null; // objectTypeが31の場合
	private Bit4 frequencyIndex = new Bit4(); // samplingFrequenceIndexと同じ
	private Bit24 frequency = null; // indexが15の場合
	private Bit4 channelConfiguration = new Bit4();
	private Bit1 frameLengthFlag = new Bit1(); // 0:each packetcontains 1024 samples 1:960 samples
	private Bit1 dependsOnCoreCoder = new Bit1();
	private Bit1 extensionFlag = new Bit1();
}
