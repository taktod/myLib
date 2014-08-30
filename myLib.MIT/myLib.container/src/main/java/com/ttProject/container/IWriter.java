/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container;

import com.ttProject.frame.CodecType;
import com.ttProject.frame.IFrame;

/**
 * ファイル書き込み
 * コンテナからも作成可能にするが、それ以上にframeから作成可能にしておきたい
 * @author taktod
 */
public interface IWriter {
	/**
	 * コンテナデータを追加
	 * いきなり非推奨って・・・
	 * @param container
	 */
	@Deprecated
	public void addContainer(IContainer container) throws Exception;
	/**
	 * メディアフレームを追加
	 * @param trackId
	 * @param frame
	 */
	public void addFrame(int trackId, IFrame frame) throws Exception;
	/**
	 * headerデータを構築
	 */
	public void prepareHeader(CodecType ...codecs) throws Exception;
	/**
	 * 終端データを構築
	 */
	public void prepareTailer() throws Exception;
}
