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
import com.ttProject.unit.extra.EbmlValue;

/**
 * CodecIDタグ
 * @author taktod
 */
public class CodecID extends MkvStringTag {
	/**
	 * コンストラクタ
	 * @param size
	 */
	public CodecID(EbmlValue size) {
		super(Type.CodecID, size);
	}
	/**
	 * コンストラクタ
	 */
	public CodecID() {
		this(new EbmlValue());
	}
	/**
	 * codecTypeを参照
	 * @return
	 * @throws Exception
	 */
	public MkvCodecType getCodecType() throws Exception {
		String name = getValue();
		if(name == null) {
			throw new Exception("loadを実行して実体を取得してください。");
		}
		return MkvCodecType.getCodecType(name);
	}
	public void setCodecType(MkvCodecType codecType) throws Exception {
		setValue(codecType.toString());
	}
}
