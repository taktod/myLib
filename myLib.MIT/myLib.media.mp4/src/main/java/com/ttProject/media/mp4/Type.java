/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.media.mp4;

/**
 * Atomタイプ
 * @author taktod
 */
public enum Type {
	NONE,
	Ftyp,
	Moov,
	  Mvhd,
	  Iods,
	  Trak,
	    Tkhd,
	    Edts,
	      Elst,
	    Mdia,
	      Mdhd,
	      Hdlr,
	      Minf,
	        Vmhd,
	        Smhd,
	        Hmhd,
	        Nmhd,
	        Dinf,
	          Dref,
	        Stbl,
	          Stsd,
	          Stts,
	          Ctts,
	          Stsc,
	          Stsz,
	          Stco,
	          Co64,
	          Stss,
	  Udta,
	Mdat,
	Free,
	Skip;
	public static Type getType(String tag) {
		String data = tag.substring(0, 1).toUpperCase() + tag.substring(1);
		try {
			return valueOf(data);
		}
		catch(Exception e) {
			return NONE;
		}
	}
}
