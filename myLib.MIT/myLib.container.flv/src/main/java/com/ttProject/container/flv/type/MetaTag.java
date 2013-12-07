package com.ttProject.container.flv.type;

import java.nio.ByteBuffer;
import java.util.LinkedHashMap;
import java.util.Map;

import com.ttProject.container.flv.FlvTag;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.Bit8;

/**
 * metaデータ
 * TODO このタグは、前のプログラムでは動作が微妙だったので・・・(sizeがふらふらかわって使いにくい)
 * 今回はもっとしっかり動作するようにしたいところ。
 * たしかデータを追加すると、サイズがかわってしまって、タイミングによっては正しいデータがとれないとかあったはず。
 * データとして、mapの形し、書き込み時に復元するとデータサイズがかわってしまうのが、難点だった・・・
 * さて、どうするかね・・・
 * データが追加されたら、bufferの中身を書き換えるみたいな動作が一番いいかもしれないね。
 * @author taktod
 */
public class MetaTag extends FlvTag {
	/** 先頭に入る文字列(固定) */
	private final String title = "onMetaData";
	/** メタデータの中身 */
	private final Map<String, Object> data = new LinkedHashMap<String, Object>();
	/** 中身の生データ */
	private ByteBuffer rawData = null;
	/**
	 * コンストラクタ
	 * @param tagType
	 */
	public MetaTag(Bit8 tagType) {
		super(tagType);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(IReadChannel channel) throws Exception {
		// こちらのloadで、ファイル上にあるデータで上書きすることにする。
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		// metaデータのminimumLoadではなにもしない。
		super.minimumLoad(channel);
		// ここで読み込み位置を記録しておいて、あとで読み込む場合に、そこから読み込めばよいみたいな感じにしておきたいですね。
		channel.position();
	}
	public void put(String key, Object value) {
		rawData = null;
		data.put(key, value);
	}
	public void remove(String key) {
		rawData = null;
		data.remove(key);
	}
	public Object get(String key) {
		return data.get(key);
	}
}
