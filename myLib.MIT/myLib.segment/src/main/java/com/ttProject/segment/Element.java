/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.segment;

public abstract class Element {
	private String file;
	private String http;
	private float duration;
	private int index;
	public Element(String file, String http, float duration, int index) {
		this.file = file;
		this.http = http;
		this.duration = duration;
		this.index = index;
	}
	public String getFile() {
		return file;
	}
	public String getHttp() {
		return http;
	}
	public float getDuration() {
		return duration;
	}
	public int getIndex() {
		return index;
	}
	
}
