/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.chunk.mpegts;

import com.ttProject.media.mpegts.CodecType;
import com.ttProject.media.mpegts.field.PmtElementaryField;
import com.ttProject.media.mpegts.packet.Pmt;

/**
 * videoDataListとaudioDataListの共通部分
 * @author taktod
 */
public abstract class MediaDataList {
	/** 動作pid */
	private short pid;
	/** 動作コーデック */
	private CodecType type = null;
	/** pcr設定 */
	private boolean pcrFlg = false;
	/**
	 * pmtから必要な情報を取り出しておく。
	 * @param pmt
	 * @param field
	 * @throws Exception
	 */
	public void analyzePmt(Pmt pmt, PmtElementaryField field) throws Exception {
		pid = field.getPid();
		type = field.getCodecType();
		pcrFlg = (pid == pmt.getPcrPid());
	}
	/**
	 * pcrであるか判定
	 * @return true:pcr false:pcrではない
	 */
	public boolean isPcr() {
		return pcrFlg;
	}
	/**
	 * pid参照
	 * @return pid値
	 */
	public short getPid() {
		return pid;
	}
	/**
	 * コーデック情報参照
	 * @return
	 */
	public CodecType getCodecType() {
		return type;
	}
}
