package com.ttProject.flazr;

import org.jboss.netty.buffer.ChannelBuffers;

import com.flazr.io.flv.FlvAtom;
import com.ttProject.media.flv.Tag;

/**
 * tagを操作する動作
 * @author taktod
 */
public class TagManager {
	/**
	 * Tagデータをflazrのatomに変換します。
	 * @param tag
	 * @return
	 * @throws Exception
	 */
	public FlvAtom getAtom(Tag tag) throws Exception {
		return new FlvAtom(ChannelBuffers.copiedBuffer(tag.getBuffer()));
	}
}