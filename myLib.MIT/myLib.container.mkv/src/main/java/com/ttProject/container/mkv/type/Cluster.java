/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.mkv.type;

import com.ttProject.container.mkv.MkvMasterTag;
import com.ttProject.container.mkv.Type;
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
	public Cluster() {
		this(new EbmlValue());
	}
}
