package com.ttProject.media.raw;

import java.awt.image.BufferedImage;

/**
 * ベーシックなビデオデータ
 * @author taktod
 * 1ミリ秒単位で再生位置を保持しておきます。
 */
public class VideoData {
	/** timestampデータ */
	private long timestamp;
	/** 表示する画像データ */
	private BufferedImage image;
	/**
	 * コンストラクタ
	 * @param image
	 */
	public VideoData(BufferedImage image) {
		this(image, -1);
	}
	/**
	 * コンストラクタ
	 */
	public VideoData(BufferedImage image, long timestamp) {
		this.image = image;
		this.timestamp = timestamp;
	}
	/**
	 * イメージ参照
	 * @return
	 */
	public BufferedImage getImage() {
		return image;
	}
	public long getTimestamp() {
		return timestamp;
	}
	@Override
	public String toString() {
		return "" + timestamp;
	}
}