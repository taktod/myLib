/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.chunk.mpegts.analyzer;

import java.util.List;

import com.ttProject.media.IAudioData;
import com.ttProject.media.mpegts.packet.Pes;

/**
 * pesからIAudioDataにするためのAnalyerのinterface
 * @author taktod
 */
public interface IAudioDataAnalyzer {
	/**
	 * pesからデータを取り出す
	 * @param pes
	 * @return
	 */
	public List<IAudioData> analyzeAudioData(Pes pes);
	/**
	 * 残っているデータを取得する
	 * (pesからデータを取り出したときに、次のデータが必ず残る
	 * 最終のデータのときに、次のpayloadの始まりがこないので、その分を取り出すのに利用します)
	 * @return
	 */
	public List<IAudioData> getRemainData();
	/**
	 * 処理中のpts値の参照動作
	 * @return
	 */
	public long getLastPtsValue();
}
