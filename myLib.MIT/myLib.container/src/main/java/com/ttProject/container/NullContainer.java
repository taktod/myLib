/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container;

import com.ttProject.nio.channels.IReadChannel;

/**
 * 空のコンテナ
 * @author taktod
 */
public class NullContainer extends Container {
	/** インスタンス */
	private static final NullContainer instance = new NullContainer();
	/**
	 * 応答として代表のインスタンスを応答します
	 * @return
	 */
	public static NullContainer getInstance() {
		return instance;
	}
	/**
	 * コンストラクタ(privateにして他では作成禁止)
	 */
	private NullContainer() {
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		throw new RuntimeException("NullContainerのデータ読み込みが要求されました。");
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(IReadChannel channel) throws Exception {
		throw new RuntimeException("NullContainerのデータ読み込みが要求されました。");
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
		throw new RuntimeException("NullContainerのデータ更新が要求されました。");
	}
}
