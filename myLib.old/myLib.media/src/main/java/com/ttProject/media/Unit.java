/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.media;

import com.ttProject.nio.channels.IReadChannel;

/**
 * ファイル上のmediaの基本ベース
 * @author taktod
 */
public abstract class Unit {
	/** データサイズ */
	private int size;
	/** ファイル上の位置 */
	private int position;
	/**
	 * コンストラクタ
	 * @param position
	 * @param size
	 */
	public Unit(int position, int size) {
		this.position = position;
		this.size = size;
	}
	/**
	 * 解析動作
	 * @param ch
	 * @param analyzer
	 * @throws Exception
	 */
	public abstract void analyze(IReadChannel ch, IAnalyzer<?> analyzer) throws Exception;
	/**
	 * 解析動作
	 * @param ch
	 * @throws Exception
	 */
	public void analyze(IReadChannel ch) throws Exception {
		analyze(ch, null);
	}
	/**
	 * データサイズ取得(タグを含んだ全部のサイズ)
	 * @return
	 */
	public int getSize() {
		return size;
	}
	/**
	 * データサイズ設定
	 * @param size
	 */
	public void setSize(int size) {
		this.size = size;
	}
	/**
	 * データ位置取得(ファイル上の位置情報)
	 * @return
	 */
	public int getPosition() {
		return position;
	}
	/**
	 * 位置情報(ファイルから読み出した場合の位置情報)
	 * @param position
	 */
	public void setPosition(int position) {
		this.position = position;
	}
}
