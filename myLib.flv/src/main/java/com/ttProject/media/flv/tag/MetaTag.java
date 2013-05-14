package com.ttProject.media.flv.tag;

import com.ttProject.media.flv.Tag;
import com.ttProject.nio.channels.IFileReadChannel;

/**
 * medaデータ
 * @author taktod
 */
public class MetaTag extends Tag {
	/**
	 * コンストラクタ
	 * @param size
	 * @param position
	 * @param timestamp
	 */
	public MetaTag(final int size, final int position, final int timestamp) {
		super(size, position, timestamp);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void analyze(IFileReadChannel ch) throws Exception {

	}
	@Override
	public String toString() {
		return "meta:" + Integer.toHexString(getTimestamp());
	}
}
