package com.ttProject.frame.aac;

import com.ttProject.unit.extra.Bit1;
import com.ttProject.unit.extra.Bit4;
import com.ttProject.unit.extra.Bit5;

/**
 * aacのdecode specific infoのデータから
 * @see http://wiki.multimedia.cx/index.php?title=MPEG-4_Audio
 * @author taktod
 */
public class DecoderSpecificInfo {
	private Bit5 objectType1; // profileの事
//	private Bit6 objectType2;
	private Bit4 frequencyIndex; // samplingFrequenceIndexと同じ
//	private int frequency; // 24bit indexが0x0Fの場合
	private Bit4 channelConfiguration;
	private Bit1 frameLengthFlag; // 0:each packetcontains 1024 samples 1:960 samples
	private Bit1 dependsOnCoreCoder;
	private Bit1 extensionFlag;

}
