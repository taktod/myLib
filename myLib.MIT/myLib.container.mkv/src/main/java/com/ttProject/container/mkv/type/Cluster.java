/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.mkv.type;

import com.ttProject.container.mkv.MkvMasterTag;
import com.ttProject.container.mkv.Type;
import com.ttProject.frame.IFrame;
import com.ttProject.unit.extra.EbmlValue;

/**
 * Clusterタグ
 * @author taktod
 */
public class Cluster extends MkvMasterTag {
	/**
	 * コンストラクタ
	 * @param size
	 */
	public Cluster(EbmlValue size) {
		super(Type.Cluster, size);
	}
	/**
	 * コンストラクタ
	 */
	public Cluster() {
		this(new EbmlValue());
	}
	/**
	 * コンストラクタ
	 * @param position
	 */
	public Cluster(long position) {
		this();
		setPosition((int)position);
	}
	/**
	 * 位置を設定する
	 * @param position
	 */
	public void setPosition(long position) {
		super.setPosition((int)position);
	}
	/**
	 * pts情報を設定する
	 */
	@Override
	public void setPts(long pts) {
		// このptsがtimecodeに影響を与えるものとします
		super.setPts(pts);
	}
	/**
	 * 保持するフレームを追加します
	 * @param trackId
	 * @param frame
	 */
	public Cluster addFrame(int trackId, IFrame frame) {
		// このデータがcluster内のsimpleBlockになります。
		// 追加していくけど、次のclusterが来たときに、実は次のclusterにいれるべきデータがでてくるかもしれないので注意が必要
		return null;
	}
}
