/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.media;

/**
 * 映像の生データに追加することで、必要なデータを参照できるようにします。
 * 具体的にはh264やvp8、flv等となります。
 * @author taktod
 * audioとは違い、連続的にあるデータではないので、xuggleやffmpegの出力、ファイルデータのpts、dts値をそのまま使えば問題ないと思われます。
 */
public interface IVideoData extends IMediaData {
	/**
	 * 横幅参照
	 * @return
	 */
	public int getWidth();
	/**
	 * 縦幅参照
	 * @return
	 */
	public int getHeight();
}
