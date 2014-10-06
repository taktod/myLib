/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.mkv.type;

import com.ttProject.container.mkv.MkvCodecType;
import com.ttProject.container.mkv.MkvStringTag;
import com.ttProject.container.mkv.Type;
import com.ttProject.frame.CodecType;
import com.ttProject.unit.extra.EbmlValue;

/**
 * CodecID
 * @author taktod
 */
public class CodecID extends MkvStringTag {
	private CodecType extraCodecType = null;
	/**
	 * constructor
	 * @param size
	 */
	public CodecID(EbmlValue size) {
		super(Type.CodecID, size);
	}
	/**
	 * constructor
	 */
	public CodecID() {
		this(new EbmlValue());
	}
	/**
	 * ref mkvCodecType
	 * @return
	 * @throws Exception
	 */
	public MkvCodecType getMkvCodecType() throws Exception {
		String name = getValue();
		if(name == null) {
			throw new Exception("load() is required, make body.");
		}
		return MkvCodecType.getMkvCodecType(name);
	}
	/**
	 * ref codecType
	 * @return
	 * @throws Exception
	 */
	public CodecType getCodecType() throws Exception {
		if(extraCodecType == null) {
			return getMkvCodecType().getCodecType();
		}
		return extraCodecType;
	}
	/**
	 * set MkvCodecType
	 * @param mkvCodecType
	 * @throws Exception
	 */
	public void setMkvCodecType(MkvCodecType mkvCodecType) throws Exception {
		setValue(mkvCodecType.toString());
	}
	/**
	 * set CodecType
	 * @param codecType
	 * @throws Exception
	 */
	public void setCodecType(CodecType codecType) throws Exception {
		switch(codecType) {
		case ADPCM_IMA_WAV:
			setValue(MkvCodecType.A_MS_ACM.toString());
			extraCodecType = codecType;
			break;
		default:
			setValue(MkvCodecType.getCodecType(codecType).toString());
			break;
		}
	}
}
