package com.ttProject.chunk.aac;

import java.util.ArrayList;
import java.util.List;

import com.ttProject.media.aac.frame.Aac;

/**
 * aacのframeをためる動作、ここにデータをためてaacChunkManagerに必要に応じてデータを取り出させる。
 * コーデックデータは一定であることを期待します。
 * @author taktod
 */
public class AacDataList {
	/** 保持データリスト */
	private final List<Aac> aacDataList = new ArrayList<Aac>();
	/** 取得フレーム数 */
	private long counter;
	/** 転送済みフレーム数 */
	private long sendedCounter = 0;
	/** サンプルレート */
	private int sampleRate = -1;
	/**
	 * 保持データリストサイズ参照
	 * @return
	 */
	public int getListCount() {
		return aacDataList.size();
	}
	/**
	 * データを追加する
	 * @param data
	 */
	public void addAacData(Aac frame) {
		counter += frame.getSampleNum();
		sampleRate = frame.getSampleRate();
		aacDataList.add(frame);
	}
	/**
	 * 保持データの終端のframeカウント
	 * @return
	 */
	public long getCounter() {
		return counter;
	}
	/**
	 * 保持データの先頭のframeカウント
	 * @return
	 */
	public long getFirstCounter() {
		return sendedCounter;
	}
	/**
	 * 動作サンプルレートを応答
	 * @return
	 */
	public int getSampleRate() {
		return sampleRate;
	}
	/**
	 * 先頭データを取り出す
	 * @return
	 */
	public Aac shift() {
		if(aacDataList.size() == 0) {
			return null;
		}
		Aac frame = aacDataList.remove(0);
		sendedCounter += frame.getSampleNum();
		return frame;
	}
	/**
	 * 先頭にデータを戻す
	 * @param frame
	 */
	public void unshift(Aac frame) {
		if(frame == null) {
			return;
		}
		sendedCounter -= frame.getSampleNum();
		aacDataList.add(0, frame);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder data = new StringBuilder();
		data.append("mp3DataList:").append(aacDataList.size());
		return data.toString();
	}
}
