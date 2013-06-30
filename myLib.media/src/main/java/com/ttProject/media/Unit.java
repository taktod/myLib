package com.ttProject.media;

/**
 * ファイル上のmediaの基本ベース
 * @author taktod
 */
public abstract class Unit {
	/** データサイズ */
	private final long size;
	/** ファイル上の位置 */
	private final long position;
	/**
	 * コンストラクタ
	 * @param position
	 * @param size
	 */
	public Unit(long position, long size) {
		this.position = position;
		this.size = size;
	}
	/**
	 * データサイズ取得(タグを含んだ全部のサイズ)
	 * @return
	 */
	public long getSize() {
		return size;
	}
	/**
	 * データ位置取得
	 * @return
	 */
	public long getPosition() {
		return position;
	}
}
