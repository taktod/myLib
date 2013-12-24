package com.ttProject.frame;

import com.ttProject.unit.ISelector;

/**
 * 映像データのコンテナから取得したデータも取り扱うselector
 * @author taktod
 */
public abstract class VideoSelector implements ISelector {
	// 以下コンテナから読み取れるデフォルト、フレームのデータを構築するときに読み込むことにします。
	/** 横幅(デフォルト設定用) */
	private int width;
	/** 縦幅(デフォルト設定用) */
	private int height;
	/**
	 * コンテナから読み取った横幅設定
	 * @param width
	 */
	public void setWidth(int width) {
		this.width = width;
	}
	/**
	 * コンテナから読み取った縦幅設定
	 * @param height
	 */
	public void setHeight(int height) {
		this.height = height;
	}
	/**
	 * コンテナデータの適応
	 * @param frame
	 */
	public void setup(VideoFrame frame) {
		frame.setWidth(width);
		frame.setHeight(height);
	}
}
