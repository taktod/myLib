package com.ttProject.container.mp4;

import com.ttProject.unit.extra.bit.Bit32;

/**
 * mp4で利用するatomBoxタイプ
 * @author taktod
 * 今回はここに、moofとかのfragment mp4もいれておきたい。
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
	Moof,
	  Mfhd,
	  Traf,
	    Tfhd,
	    Trun,
	Mdat,
	Free,
	Skip,
	Mfra,
	  Tfra,
	  Mfro;
	/**
	 * 文字列から動作typeを応答します
	 * @param tag
	 * @return
	 */
	public static Type getType(String tag) {
		String data = tag.substring(0, 1).toUpperCase() + tag.substring(1);
		try {
			return valueOf(data);
		}
		catch(Exception e) {
			return NONE;
		}
	}
	/**
	 * 数値からtagTypeを取得します。
	 * @param tag
	 * @return
	 */
	public static Type getType(int tag) {
		byte[] bytes = new byte[4];
		bytes[0] = (byte)((tag >>> 24) & 0xFF);
		bytes[1] = (byte)((tag >>> 16) & 0xFF);
		bytes[2] = (byte)((tag >>> 8) & 0xFF);
		bytes[3] = (byte)(tag & 0xFF);
		String data = new String(new byte[]{bytes[0]}).toUpperCase() + 
					new String(new byte[]{bytes[1], bytes[2], bytes[3]});
		try {
			return valueOf(data);
		}
		catch(Exception e) {
			return NONE;
		}
	}
	/**
	 * typeから文字列bit32を応答します。
	 * @param type
	 * @return
	 */
	public static Bit32 getTypeBit(Type type) {
		byte[] bytes = type.toString().toLowerCase().getBytes();
		int value = bytes[0] << 24 | bytes[1] << 16 | bytes[2] << 8 | bytes[3];
		return new Bit32((int)value);
	}
}
