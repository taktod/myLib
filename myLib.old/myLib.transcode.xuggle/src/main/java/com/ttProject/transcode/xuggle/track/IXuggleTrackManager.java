/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 */
package com.ttProject.transcode.xuggle.track;

import java.util.concurrent.ExecutorService;

import com.ttProject.transcode.ITrackManager;
import com.ttProject.transcode.xuggle.packet.IDepacketizer;
import com.xuggle.xuggler.IStreamCoder;

/**
 * xuggle処理用のtrackManagerのインターフェイス部
 * @author taktod
 */
public interface IXuggleTrackManager extends ITrackManager {
	/**
	 * encoderの設定動作
	 * @param encoder
	 * @throws Exception
	 */
	public void setEncoder(IStreamCoder encoder) throws Exception;
	/**
	 * パケットを分解する動作の設定
	 * @param depacketizer
	 */
	public void setDepacketizer(IDepacketizer depacketizer);
	/**
	 * マルチスレッド動作させる場合のexecutorService登録動作
	 * @param executor
	 */
	public void setExecutorService(ExecutorService executor);
}
