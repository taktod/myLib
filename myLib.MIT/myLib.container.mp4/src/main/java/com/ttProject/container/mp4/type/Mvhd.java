/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.mp4.type;

import org.apache.log4j.Logger;

import com.ttProject.container.mp4.Mp4Atom;
import com.ttProject.container.mp4.Type;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.Bit;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit16;
import com.ttProject.unit.extra.bit.Bit24;
import com.ttProject.unit.extra.bit.Bit32;
import com.ttProject.unit.extra.bit.Bit64;
import com.ttProject.unit.extra.bit.Bit8;

/**
 * mvhdの定義
 * @author taktod
 * 64bitと32bitにわかれる部分は、Bitとしておきました。
 * 実データ以外は先に読み込んでおいてもよさそうなものだが・・・おそくなるかな・・・
 * mkvだとloadで実態がはいっているみたいなので、それに合わせておく。
 */
public class Mvhd extends Mp4Atom {
	/** ロガー */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(Mvhd.class);
	private Bit8    version = new Bit8();
	private Bit24   flags   = new Bit24();
	private Bit     creationTime     = null;
	private Bit     modificationTime = null;
	private Bit32   timeScale        = null;
	private Bit     duration         = null;
	private Bit32   playbackRate     = null;
	private Bit16   volume           = null;
	private Bit16   reserved1        = null;
	private Bit32[] reserved2        = new Bit32[2];
	private Bit32[] matrix           = new Bit32[9];
	private Bit32[] reserved3        = new Bit32[6];
	private Bit32   nextTrackId      = null; // 次追加する場合のtrackIdか？
	/**
	 * コンストラクタ
	 * @param size
	 * @param name
	 */
	public Mvhd(Bit32 size, Bit32 name) {
		super(size, name);
	}
	/**
	 * コンストラクタ
	 */
	public Mvhd() {
		super(new Bit32(), Type.getTypeBit(Type.Mvhd));
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		super.minimumLoad(channel);
		// このタイミングでversionとflagsを読み込んでおく。
		BitLoader loader = new BitLoader(channel);
		loader.load(version, flags);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(IReadChannel channel) throws Exception {
		// load実行時に必要な情報をすべて読み込んでおきます。
		BitLoader loader = new BitLoader(channel);
		if(version.get() == 0) {
			// 32bit動作
			creationTime     = new Bit32();
			modificationTime = new Bit32();
			duration         = new Bit32();
		}
		else if(version.get() == 1){
			// 64bit動作
			creationTime     = new Bit64();
			modificationTime = new Bit64();
			duration         = new Bit64();
		}
		else {
			throw new Exception("version is corrupted.");
		}
		timeScale = new Bit32();
		playbackRate = new Bit32();
		volume = new Bit16();
		reserved1 = new Bit16();
		reserved2[0] = new Bit32();
		reserved2[1] = new Bit32();
		matrix[0] = new Bit32();
		matrix[1] = new Bit32();
		matrix[2] = new Bit32();
		matrix[3] = new Bit32();
		matrix[4] = new Bit32();
		matrix[5] = new Bit32();
		matrix[6] = new Bit32();
		matrix[7] = new Bit32();
		matrix[8] = new Bit32();
		reserved3[0] = new Bit32();
		reserved3[1] = new Bit32();
		reserved3[2] = new Bit32();
		reserved3[3] = new Bit32();
		reserved3[4] = new Bit32();
		reserved3[5] = new Bit32();
		nextTrackId = new Bit32();
		loader.load(creationTime, modificationTime, timeScale, duration, playbackRate, volume);
		loader.load(reserved1);
		loader.load(reserved2);
		loader.load(matrix);
		loader.load(reserved3);
		loader.load(nextTrackId);
		super.load(channel);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
	}
}
