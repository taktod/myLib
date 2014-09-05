/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.mkv.type;

import java.nio.ByteBuffer;

import com.ttProject.container.mkv.MkvMasterTag;
import com.ttProject.container.mkv.Type;
import com.ttProject.unit.extra.EbmlValue;

/**
 * Seekタグ
 * @author taktod
 */
public class Seek extends MkvMasterTag {
	/**
	 * コンストラクタ
	 * @param size
	 */
	public Seek(EbmlValue size) {
		super(Type.Seek, size);
	}
	/**
	 * コンストラクタ
	 */
	public Seek() {
		this(new EbmlValue());
	}
	/**
	 * seek情報を作り上げておく
	 * @param type
	 * @param pos
	 * @throws Exception
	 */
	public void setup(Type type, long pos) throws Exception {
		SeekID seekId = new SeekID();
		ByteBuffer idBuffer = ByteBuffer.allocate(4);
		idBuffer.putInt(type.intValue());
		idBuffer.flip();
		seekId.setValue(idBuffer);
		addChild(seekId);
		SeekPosition position = new SeekPosition();
		position.setValue(pos);
		addChild(position);
	}
}
