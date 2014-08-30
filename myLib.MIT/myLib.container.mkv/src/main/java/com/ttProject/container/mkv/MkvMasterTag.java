/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.mkv;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ttProject.container.IContainer;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.BitConnector;
import com.ttProject.unit.extra.EbmlValue;

/**
 * 他のTagを内包するTagの動作
 * @author taktod
 * 
 * TODO segmentのタグだけ別のものとなります。
 * 一番上の要素って中身の要素が決定しないとsizeがきまらない。
 * ebmlTag + sizeを書いて中身は中の要素に書き込みさせるという感じでしょうか・・・
 */
public abstract class MkvMasterTag extends MkvTag {
	/** ロガー */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(MkvMasterTag.class);
	/** 保持タグリスト */
	private List<MkvTag> childTags = new ArrayList<MkvTag>();
	/**
	 * コンストラクタ
	 * @param tag
	 * @param size
	 */
	public MkvMasterTag(Type id, EbmlValue size) {
		super(id, size);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(IReadChannel channel) throws Exception {
		int targetSize = getMkvSize();
		IContainer container = null;
		while(targetSize > 0 && (container = getMkvTagReader().read(channel)) != null) {
			targetSize -= container.getSize();
			if(container instanceof MkvTag) {
				childTags.add((MkvTag)container);
			}
		}
		super.load(channel);
	}
	/**
	 * 子要素を追加する
	 */
	public void addChild(MkvTag tag) {
		childTags.add(tag);
		super.update();
	}
	/**
	 * 子要素を撤去する
	 * @param i
	 * @return
	 */
	public MkvTag removeChild(int i) {
		MkvTag removedTag = childTags.remove(i);
		super.update();
		return removedTag;
	}
	/**
	 * 子要素を参照する
	 * @return
	 */
	public List<MkvTag> getChildList() {
		return new ArrayList<MkvTag>(childTags);
	}
	/**
	 * サイズを無限にする
	 * @param set true:設定する false:解除する
	 */
	public void setInfinite(boolean set) {
		if(set) {
			getTagSize().setLong(0xFFFFFFFFFFFFFFL);
		}
		else {
			getTagSize().set(0);
		}
		super.update();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
		// ここで子要素のサイズについて、調査しておけばいいかな？
		if(getTagSize().getLong() == 0xFFFFFFFFFFFFFFL) {
			// とりあえず、headerとsizeの部分だけ応答として返したい
			BitConnector connector = new BitConnector();
			super.setData(connector.connect(getTagId(), getTagSize()));
			return;
		}
		int size = 0;
		for(MkvTag tag : childTags) {
			size += tag.getData().remaining();
		}
		getTagSize().set(size);
		// とりあえず、headerとsizeの部分だけ応答として返したい
		BitConnector connector = new BitConnector();
		super.setData(connector.connect(getTagId(), getTagSize()));
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString(String space) {
		StringBuilder data = new StringBuilder();
		data.append(super.toString(space));
		data.append("*");
		return data.toString();
	}
}
