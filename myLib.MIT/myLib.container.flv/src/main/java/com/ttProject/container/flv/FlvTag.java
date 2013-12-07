package com.ttProject.container.flv;

import java.nio.ByteBuffer;

import com.ttProject.container.IContainer;

/**
 * flvデータのタグ
 * @author taktod
 */
public abstract class FlvTag implements IContainer {
	
	/**
	 * コンストラクタ
	 */
	public FlvTag() {
		
	}
	/**
	 * {@inheritDoc}
	 * こちらはファイル上に展開させた場合の位置となります。
	 * rtmpで転送されてきたデータからつくった場合とかは重要にはなりません。
	 */
	@Override
	public int getPosition() {
		return 0;
	}
	/**
	 * {@inheritDoc}
	 * flvは全体サイズとは別に２つのサイズがあるので注意
	 */
	@Override
	public long getSize() {
		return 0;
	}
	/**
	 * {@inheritDoc}
	 * tagの全体のデータを応答します
	 */
	@Override
	public ByteBuffer getData() throws Exception {
		return null;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public long getPts() {
		return 0;
	}
	/**
	 * {@inheritDoc}
	 * flvは1000固定です。
	 */
	@Override
	public long getTimebase() {
		return 1000L;
	}
}
