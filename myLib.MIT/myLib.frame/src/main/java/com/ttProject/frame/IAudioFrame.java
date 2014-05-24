/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame;

/**
 * 音声フレームのインターフェイス定義
 * @author taktod
 */
public interface IAudioFrame extends IFrame {
	/**
	 * unitの持つサンプル数を応答します
	 * @return
	 */
	public int getSampleNum();
	/**
	 * unitのサンプルレートを応答します。
	 * @return
	 */
	public int getSampleRate();
	/**
	 * unitのチャンネル数を応答します。
	 * @return
	 */
	public int getChannel();
	/**
	 * サンプルの動作bitを応答します。
	 * @return
	 */
	public int getBit();
}
