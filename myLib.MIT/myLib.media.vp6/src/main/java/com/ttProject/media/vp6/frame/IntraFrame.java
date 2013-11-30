package com.ttProject.media.vp6.frame;

import com.ttProject.media.extra.Bit1;
import com.ttProject.media.extra.Bit2;
import com.ttProject.media.extra.Bit5;
import com.ttProject.media.extra.Bit8;

/**
 * キーフレーム
 * @author taktod
 */
public class IntraFrame {
	
	private Bit5 version;
	private Bit2 version2;
	private Bit1 interlace;
	
	private short offset; // 16bit
	
	private Bit8 dimY; // x16で縦幅
	private Bit8 dimX; // x16で横幅
	private Bit8 renderY; // x16で縦幅
	private Bit8 renderX; // x16で横幅

}
