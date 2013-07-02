package com.ttProject.media;

import com.ttProject.nio.channels.IFileReadChannel;

/**
 * ファイル上のmediaの基本ベース
 * @author taktod
 */
public abstract class Unit {
	/** データサイズ */
	private int size;
	/** ファイル上の位置 */
	private int position;
	/**
	 * コンストラクタ
	 * @param position
	 * @param size
	 */
	public Unit(int position, int size) {
		this.position = position;
		this.size = size;
	}
	/**
	 * 解析動作
	 * @param ch
	 * @param analyzer
	 * @throws Exception
	 */
	public abstract void analyze(IFileReadChannel ch, IAnalyzer<?> analyzer) throws Exception;
	/**
	 * 解析動作
	 * @param ch
	 * @throws Exception
	 */
	public void analyze(IFileReadChannel ch) throws Exception {
		analyze(ch, null);
	}
	/**
	 * データサイズ取得(タグを含んだ全部のサイズ)
	 * @return
	 */
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	/**
	 * データ位置取得
	 * @return
	 */
	public int getPosition() {
		return position;
	}
	public void setPosition(int position) {
		this.position = position;
	}
}
