package com.ttProject.chunk.mp3;

import java.util.ArrayList;
import java.util.List;

import com.ttProject.media.mp3.frame.Mp3;

/**
 * mp3のframeをためる動作、ここにデータをためておくと、mp3ChunkManagerが必要に応じてmp3frameを取り出してchunk化する。
 * とりあえずmp3のコーデックデータは一定であることを期待します。
 * コーデックデータがかわったらEXT-DISCONTINUITYかけて、別のファイルとしてやり直した方がいいと思う。
 * @author taktod
 */
public class Mp3DataList {
	/** 保持データリスト */
	private final List<Mp3> mp3DataList = new ArrayList<Mp3>();
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
		return mp3DataList.size();
	}
	/**
	 * データを追加する
	 * @param data
	 */
	public void addMp3Data(Mp3 frame) {
		counter += frame.getSampleNum();
		sampleRate = frame.getSampleRate();
		mp3DataList.add(frame);
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
	public Mp3 shift() {
		if(mp3DataList.size() == 0) {
			return null;
		}
		Mp3 frame = mp3DataList.remove(0);
		sendedCounter += frame.getSampleNum();
		return frame;
	}
	/**
	 * 先頭にデータを戻す
	 * @param frame
	 */
	public void unshift(Mp3 frame) {
		if(frame == null) {
			return;
		}
		sendedCounter -= frame.getSampleNum();
		mp3DataList.add(0, frame);
	}
	@Override
	public String toString() {
		StringBuilder data = new StringBuilder();
		data.append("mp3DataList:").append(mp3DataList.size());
		return data.toString();
	}
}
