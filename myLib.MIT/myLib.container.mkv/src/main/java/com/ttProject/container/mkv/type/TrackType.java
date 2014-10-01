/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.mkv.type;

import com.ttProject.container.mkv.MkvUnsignedIntTag;
import com.ttProject.container.mkv.Type;
import com.ttProject.unit.extra.EbmlValue;

/**
 * TrackTypeタグ
 * @author taktod
 */
public class TrackType extends MkvUnsignedIntTag {
	/**
	 * コンストラクタ
	 * @param size
	 */
	public TrackType(EbmlValue size) {
		super(Type.TrackType, size);
	}
	/**
	 * コンストラクタ
	 */
	public TrackType() {
		this(new EbmlValue());
	}
	/**
	 * タイプ情報を参照する
	 * @return
	 * @throws Exception
	 */
	public Media getType() throws Exception {
		return Media.getType((int)super.getValue());
	}
	/**
	 * タイプ情報を設定する
	 * @param media
	 * @throws Exception
	 */
	public void setType(Media media) throws Exception {
		super.setValue(media.value);
	}
	public static enum Media {
		Video(0x01),
		Audio(0x02),
		Complex(0x03),
		Logo(0x10),
		Subtitle(0x11),
		Buttons(0x20);
		private final int value;
		private Media(int value) {
			this.value = value;
		}
		public static Media getType(int value) throws Exception {
			for(Media t : values()) {
				if(t.value == value) {
					return t;
				}
			}
			throw new Exception("type is unknown.:" + value);
		}
	}
}
