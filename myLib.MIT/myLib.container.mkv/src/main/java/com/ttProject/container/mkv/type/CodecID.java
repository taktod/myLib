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
	 * mkvCodecTypeを参照
	 * @return
	 * @throws Exception
	 */
	public MkvCodecType getMkvCodecType() throws Exception {
		String name = getValue();
		if(name == null) {
			throw new Exception("loadを実行して実体を取得してください。");
		}
		return MkvCodecType.getCodecType(name);
	}
	/**
	 * codecTypeを参照
	 * @return
	 * @throws Exception
	 */
	public CodecType getCodecType() throws Exception {
		return getMkvCodecType().getCodecType();
	}
	/**
	 * CodecIDを設定する
	 * @param mkvCodecType
	 * @throws Exception
	 */
	public void setMkvCodecType(MkvCodecType mkvCodecType) throws Exception {
		setValue(mkvCodecType.toString());
	}
	/**
	 * CodecIDを設定する
	 * @param codecType
	 * @throws Exception
	 */
	public void setCodecType(CodecType codecType) throws Exception {
		setValue(MkvCodecType.getCodecType(codecType).toString());
	}
}
