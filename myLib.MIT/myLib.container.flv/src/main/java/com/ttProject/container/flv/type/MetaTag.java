/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.flv.type;

import java.nio.ByteBuffer;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.ttProject.container.flv.FlvTag;
import com.ttProject.container.flv.amf.Amf0Value;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.bit.Bit8;
import com.ttProject.util.BufferUtil;

/**
 * metaTag
 * 12 xx xx xx tt tt tt tt 00 00 00 [AMF0(onMetaData(string))] [AMF0ObjectMapData] xx xx xx xx
 * 
 * TODO このタグは、前のプログラムでは動作が微妙だったので・・・(sizeがふらふらかわって使いにくい)
 * 今回はもっとしっかり動作するようにしたいところ。
 * たしかデータを追加すると、サイズがかわってしまって、タイミングによっては正しいデータがとれないとかあったはず。
 * データとして、mapの形し、書き込み時に復元するとデータサイズがかわってしまうのが、難点だった・・・
 * さて、どうするかね・・・
 * データが追加されたら、bufferの中身を書き換えるみたいな動作が一番いいかもしれないね。
 * @author taktod
 */
public class MetaTag extends FlvTag {
	/** logger */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(MetaTag.class);
	/** string(fixed) */
	private final String title = "onMetaData";
	/** metaData */
	private final Map<String, Object> data = new LinkedHashMap<String, Object>();
	/** correspond buffer. */
	private ByteBuffer rawBuffer = null;
	/**
	 * constructor
	 * @param tagType
	 */
	public MetaTag(Bit8 tagType) {
		super(tagType);
	}
	/**
	 * constructor
	 */
	public MetaTag() {
		this(new Bit8(0x12));
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(IReadChannel channel) throws Exception {
		channel.position(getPosition() + 11);
		// load the all Data on rawBuffer first.
		rawBuffer = BufferUtil.safeRead(channel, getSize() - 15);
		// analyzeData.
		IReadChannel bufferChannel = new ByteReadChannel(rawBuffer.duplicate());
		String tag = (String)Amf0Value.getValueObject(bufferChannel);
		if(!title.equals(tag)) {
			throw new Exception("start code is not onMetaData");
		}
		// check the data.
		while(bufferChannel.position() < bufferChannel.size()) {
			Object data = Amf0Value.getValueObject(bufferChannel);
			if(!(data instanceof Map<?, ?>)) {
				throw new Exception("data is not described by map. unexpected data.");
			}
			@SuppressWarnings("unchecked")
			Map<String, Object> object = (Map<String, Object>) data;
			for(Entry<String, Object> entry : object.entrySet()) {
				this.data.put(entry.getKey(), entry.getValue());
			}
		}
		// check prevTagSize.
		if(getPrevTagSize() != BufferUtil.safeRead(channel, 4).getInt()) {
			throw new Exception("size data is corrupted.");
		}
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		super.minimumLoad(channel);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
		if(rawBuffer == null) {
			throw new Exception("rawBuffer is undefined.");
		}
		ByteBuffer startBuffer = getStartBuffer();
		ByteBuffer tailBuffer = getTailBuffer();
		// rawBuffer is ref only once(if changed, make rawBuffer again.)
		setData(BufferUtil.connect(
				startBuffer,
				rawBuffer,
				tailBuffer
		));
	}
	/**
	 * add data.
	 * @param key
	 * @param value
	 * @throws Exception
	 */
	public void put(String key, Object value) throws Exception {
		data.put(key, value);
		updateData();
	}
	/**
	 * remove data
	 * @param key
	 * @throws Exception
	 */
	public void remove(String key) throws Exception {
		data.remove(key);
		updateData();
	}
	/**
	 * data and size are changed by metaData update.
	 * the position of all flvTag is changed. so, position of file is nonsence now.
	 * @throws Exception
	 */
	private void updateData() throws Exception {
		rawBuffer = BufferUtil.connect(
				Amf0Value.getValueBuffer(title),
				Amf0Value.getValueBuffer(data));
		setSize(rawBuffer.remaining() + 15);
		update();
	}
	/**
	 * ref the data.
	 * @param key
	 * @return
	 */
	public Object get(String key) {
		return data.get(key);
	}
}
