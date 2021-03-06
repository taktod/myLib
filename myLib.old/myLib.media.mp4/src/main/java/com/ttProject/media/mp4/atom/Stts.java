/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.media.mp4.atom;

import java.nio.ByteBuffer;

import com.ttProject.media.mp4.Atom;
import com.ttProject.media.mp4.IAtomAnalyzer;
import com.ttProject.nio.CacheBuffer;
import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IFileReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.util.BufferUtil;

public class Stts extends Atom {
	private int count;
	
	private CacheBuffer buffer;
	private int cnt = 0;
	private int delta = 0;
	public Stts(int position, int size) {
		super(Stts.class.getSimpleName().toLowerCase(), position, size);
	}
	@Override
	public void analyze(IReadChannel ch, IAtomAnalyzer analyzer) throws Exception {
		ch.position(getPosition() + 8);
		ByteBuffer buffer = BufferUtil.safeRead(ch, 8);
		analyzeFirstInt(buffer.getInt());
		count = buffer.getInt();
		// このあとのデータはサンプルカウント デルタ長(実際の時間に治すにはtimescale値を参照する必要あり)(両方int)
		/*
		 * 5,3 4,2 2,1となっている場合
		 * 1,3
		 * 2,3
		 * 3,3
		 * 4,3
		 * 5,3
		 * 6,2
		 * 7,2
		 * 8,2
		 * 9,2
		 * 10,1
		 * 11,1
		 * ...となる
		 */
	}
	public void start(IReadChannel src, boolean copy) throws Exception {
		IReadChannel source;
		if(copy) {
			if(!(src instanceof IFileReadChannel)) {
				throw new Exception("IFileReadChannel系のreadChannelでないと、オブジェクトのcloneは作成不能です");
			}
			source = FileReadChannel.openFileReadChannel(((IFileReadChannel)src).getUri());
		}
		else {
			source = src;
		}
		source.position(getPosition() + 16);
		buffer = new CacheBuffer(source, getSize() - 16);
	}
	public int nextDuration() throws Exception {
		if(cnt != 0) {
			cnt --;
			return delta;
		}
		if(buffer.remaining() == 0) {
			return -1;
		}
		// 読み込みデータがのこっているか確認
		// のこっていない場合
		cnt = buffer.getInt();
		delta = buffer.getInt();
		cnt --;
		return delta;
	}
	public int getDuration() {
		return delta;
	}
	public int getCount() {
		return count;
	}
	@Override
	public String toString() {
		return super.toString("          ");
	}
}
