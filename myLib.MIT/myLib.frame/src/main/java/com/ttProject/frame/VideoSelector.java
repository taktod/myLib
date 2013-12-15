package com.ttProject.frame;

import com.ttProject.unit.ISelector;

/**
 * 映像データのコンテナから取得したデータも取り扱うselector
 * @author taktod
 */
public abstract class VideoSelector implements ISelector {
	private int width;
	private int height;
	public void setWidth(int width) {
		this.width = width;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public VideoFrame setup(VideoFrame frame) {
		frame.setWidth(width);
		frame.setHeight(height);
		return frame;
	}
}
