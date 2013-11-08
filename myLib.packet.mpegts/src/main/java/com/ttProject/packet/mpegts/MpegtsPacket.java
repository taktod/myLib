package com.ttProject.packet.mpegts;

import java.nio.ByteBuffer;

import com.ttProject.packet.MediaPacket;

/**
 * このパケットデータが指定された秒数分のファイルデータとなります。
 * Sdt Pat Pmt [keyFrame Audio innerFrame] [keyFrame Audio innerFrame]
 * となるようにしておきたいと思います。
 * 
 * データの長さについては、次のkeyFrame位置から決定してもいいけど、音声データの長さで計算した方がいいかも。
 * durationなるべく全体からみて整数倍の部分できりたいところ・・・
 * @author taktod
 */
public class MpegtsPacket extends MediaPacket {
	/** audioデータのサンプリングレート */
	private int audioSampleRate;
	/** audioデータのサンプル数 */
	private int audioSampleNum = 0;
	/**
	 * audioのサンプリングレートを設定しておく。
	 * @param rate
	 */
	public void setAudioSampleRate(int rate) {
		audioSampleRate = rate;
	}
	/**
	 * サンプル数を追加する。
	 * @param num
	 */
	public void addSampleNum(int num) {
		audioSampleNum += num;
	}
	/**
	 * データの長さを参照する
	 */
	public float getDuration() {
		return (1.0f * audioSampleNum / audioSampleRate);
	}
	/**
	 * データの解析を実施します。
	 * この動作は、ここにいれられたmpegtsのbyteBufferがそのままデータとなります。
	 */
	@Override
	public boolean analize(ByteBuffer buffer) {
		// 保持しているbufferを取り出す
		ByteBuffer buf = getBuffer(buffer.remaining());
		buf.put(buffer); // 追加しておく。
		return false;
	}
	/**
	 * ヘッダーデータであるか応答する。
	 */
	@Override
	public boolean isHeader() {
		return false;
	}
	@Override
	public String toString() {
		StringBuilder data = new StringBuilder();
		data.append("size:").append(getBufferSize());
		data.append(" duration:").append(getDuration());
		return data.toString();
	}
}
