package com.ttProject.segment.m3u8;

import com.ttProject.segment.Element;

public class M3u8Element extends Element {
	private String info;
	private boolean isFirst;
	/**
	 * コンストラクタ
	 * @param file
	 * @param http
	 * @param duration
	 * @param index
	 */
	public M3u8Element(String file, String http, float duration, int index) {
		super(file, http, duration, index);
		this.info = "#EXTINF:" + duration;
		this.isFirst = index == 1;
	}
	public String getInfo() {
		return info;
	}
	public boolean isFirst() {
		return isFirst;
	}
}
