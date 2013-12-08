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
import com.ttProject.unit.extra.Bit8;
import com.ttProject.util.BufferUtil;

/**
 * metaデータ
 * 12 xx xx xx tt tt tt tt 00 00 00 [AMF0でonMetaData(文字列)] [AMF0でObjectMapデータ] xx xx xx xx
 * xxの部分はサイズ、先頭のサイズと終端のサイズと２つある。
 * ttの部分はtimestamp
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
	/** ロガー */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(MetaTag.class);
	/** 先頭に入る文字列(固定) */
	private final String title = "onMetaData";
	/** メタデータの中身 */
	private final Map<String, Object> data = new LinkedHashMap<String, Object>();
	/** 生データ部分 */
	private ByteBuffer rawBuffer = null;
	/**
	 * コンストラクタ
	 * @param tagType
	 */
	public MetaTag(Bit8 tagType) {
		super(tagType);
	}
	/**
	 * コンストラクタ
	 */
	public MetaTag() {
		this(new Bit8(0x12));
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(IReadChannel channel) throws Exception {
		// こちらのloadで、ファイル上にあるデータで上書きすることにする。
		channel.position(getPosition() + 11);
		// 読み込みを実施する。
		// 先に必要なデータを読み込んでDataに登録してから活動開始としたい。
		rawBuffer = BufferUtil.safeRead(channel, getSize() - 15);
		IReadChannel bufferChannel = new ByteReadChannel(rawBuffer.duplicate());
		String tag = (String)Amf0Value.getValueObject(bufferChannel);
		if(!title.equals(tag)) {
			throw new Exception("先頭がonMetaDataになっていませんでした。");
		}
		// このタイミングでmetaDataの中身を確認しておきます
		while(bufferChannel.position() < bufferChannel.size()) {
			Object data = Amf0Value.getValueObject(bufferChannel);
			if(!(data instanceof Map<?, ?>)) {
				throw new Exception("内部データ構成がMapではないみたいです。知らない形式です。");
			}
			@SuppressWarnings("unchecked")
			Map<String, Object> object = (Map<String, Object>) data;
			for(Entry<String, Object> entry : object.entrySet()) {
				this.data.put(entry.getKey(), entry.getValue());
			}
		}
		// prevTagSizeを確認しておく。
		if(getPrevTagSize() != BufferUtil.safeRead(channel, 4).getInt()) {
			throw new Exception("終端タグのデータ量がおかしいです。");
		}
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		// metaデータのminimumLoadではなにもしない。
		super.minimumLoad(channel);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
		if(rawBuffer == null) {
			throw new Exception("データ更新の要求がありましたが、内部データが決定していません");
		}
		ByteBuffer startBuffer = getStartBuffer();
		// この部分は、rawBufferのポインターがずれてもかまわない。更新したときのみに作り直す形になっているため。
//		ByteBuffer rawBuffer = this.rawBuffer.duplicate();
		ByteBuffer tailBuffer = getTailBuffer();
		setData(BufferUtil.connect(
				startBuffer,
				rawBuffer,
				tailBuffer
		));
	}
	public void put(String key, Object value) throws Exception {
		data.put(key, value);
		updateData();
	}
	public void remove(String key) throws Exception {
		data.remove(key);
		updateData();
	}
	/**
	 * metaDataのデータが変更した場合には、dataの変更とsizeの変更が発生します。
	 * なお、後ろにあるタグはすべてずれるので、positionの意味がなくなります。
	 * @throws Exception
	 */
	private void updateData() throws Exception {
		rawBuffer = BufferUtil.connect(
				Amf0Value.getValueBuffer(title),
				Amf0Value.getValueBuffer(data));
		setSize(rawBuffer.remaining() + 15);
		update();
	}
	public Object get(String key) {
		return data.get(key);
	}
}
