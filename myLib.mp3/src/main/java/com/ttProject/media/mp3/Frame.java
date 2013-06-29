package com.ttProject.media.mp3;

import com.ttProject.nio.channels.IFileReadChannel;

/**
 * Frameデータ
 * http://mpgedit.org/mpgedit/mpeg_format/MP3Format.html
 * @author taktod
 */
public abstract class Frame {
	private final int size; // データサイズ
	private final int position; // ファイル上の位置
	/**
	 * コンストラクタ
	 * @param size
	 * @param position
	 */
	public Frame(final int size, final int position) {
		this.size = size;
		this.position = position;
	}
	/**
	 * 解析動作
	 * @param ch
	 * @param analyzer
	 * @throws Exception
	 */
	public abstract void analyze(IFileReadChannel ch, IFrameAnalyzer analyzer) throws Exception;
	/**
	 * 解析動作
	 * @param ch
	 * @throws Exception
	 */
	public void analyze(IFileReadChannel ch) throws Exception {
		analyze(ch, null);
	}
	public int getSize() {
		return size;
	}
	public int getPosition() {
		return position;
	}
	/**
	 * 情報表示
	 */
	@Override
	public String toString() {
		return super.toString();
	}
}
