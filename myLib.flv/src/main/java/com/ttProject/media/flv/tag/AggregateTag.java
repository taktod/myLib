package com.ttProject.media.flv.tag;

import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.List;

import com.ttProject.media.flv.Tag;
import com.ttProject.nio.channels.IFileReadChannel;

/**
 * 複数のtagをまとめて管理するtag
 * @author taktod
 */
public class AggregateTag extends Tag {
	private List<Tag> tagList = new ArrayList<Tag>();
	/**
	 * コンストラクタ
	 */
	public AggregateTag() {
		super();
	}
	/**
	 * 追加
	 * @param tag
	 */
	public void add(Tag tag) {
		tagList.add(tag);
	}
	/**
	 * リストの応答(参照用)
	 * @return
	 */
	public List<Tag> getList() {
		return new ArrayList<Tag>(tagList);
	}
	/**
	 * 要素数の応答
	 */
	public int size() {
		return tagList.size();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void analyze(IFileReadChannel ch, boolean atBegin) throws Exception {
		throw new Exception("ファイルから解析するタグではないです。");
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void writeTag(WritableByteChannel target) throws Exception {
		for(Tag tag : tagList) {
			tag.writeTag(target);
		}
	}
	@Override
	public int getRealSize() throws Exception {
		throw new Exception("aggregateTagはflvのサイズ応答をサポートしません");
	}
}
