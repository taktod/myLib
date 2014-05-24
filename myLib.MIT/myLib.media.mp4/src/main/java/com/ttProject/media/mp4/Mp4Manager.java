/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.media.mp4;

import java.lang.reflect.Constructor;
import java.nio.ByteBuffer;
import java.util.List;

import com.ttProject.media.Manager;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.util.BufferUtil;

public class Mp4Manager extends Manager<Atom> {
	/**
	 * 
	 */
	@Override
	public List<Atom> getUnits(ByteBuffer data) throws Exception {
		throw new Exception("mp4はbufferの固定していない情報読み込みには対応していません。");
	}
	@Override
	public Atom getUnit(IReadChannel source) throws Exception {
		if(source.size() - source.position() < 8) {
			return null;
		}
		int position = source.position();
		ByteBuffer buffer = BufferUtil.safeRead(source, 8);
		int size = buffer.getInt();
		String tag = BufferUtil.getDwordText(buffer);
		// TODO あまり良くないけど、refrectionをつかって処理しておく。(コード書くのが面倒)
		try {
			Class<?> cls = Thread.currentThread().getContextClassLoader().loadClass(getClassName(tag));
			if(cls == null) {
				return null;
			}
			Constructor<?> construct = cls.getConstructor(new Class<?>[]{int.class, int.class});
			return (Atom)construct.newInstance(new Object[]{position, size});
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * lowerCaseの文字列から、クラス名を取得する
	 * @param lowerTagName
	 * @return
	 */
	private String getClassName(String lowerTagName) {
		return "com.ttProject.media.mp4.atom." + lowerTagName.substring(0, 1).toUpperCase() + lowerTagName.substring(1).toLowerCase();
	}
}
