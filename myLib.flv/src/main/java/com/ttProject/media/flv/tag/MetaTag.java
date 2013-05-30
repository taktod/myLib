package com.ttProject.media.flv.tag;

import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.LinkedHashMap;
import java.util.Map;

import com.ttProject.media.flv.Tag;
import com.ttProject.media.flv.amf.Amf0Value;
import com.ttProject.nio.channels.IFileReadChannel;

/**
 * medaデータ
 * 12 size timestamp 00 00 00 02 
 * 文字列 onMetaData 02 size(short) データ
 * Map 08 int値
 *   文字列 データの繰り返し
 * 00 00 09(eof for map) 
 * 
 * @author taktod
 */
public class MetaTag extends Tag {
	/** メタデータの基本文字列 */
	private final String title = "onMetaData";
	/** メタデータの中身 */
	private final Map<String, Object> data = new LinkedHashMap<String, Object>();
	/**
	 * データの設定
	 * @param key
	 * @param data
	 */
	public void putData(String key, Object data) {
		this.data.put(key, data);
	}
	/**
	 * データの参照
	 * @param key
	 * @return
	 */
	public Object getData(String key) {
		return data.get(key);
	}
	/**
	 * コンストラクタ
	 */
	public MetaTag() {
		super();
	}
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
	public void analyze(IFileReadChannel ch, boolean atBegin) throws Exception {
		// ファイルから読み込んでなんとかしておく。
		if(atBegin) {
			// 先頭部分のデータを読み込んでおく。
		}
		// データを読み込む
		String tag = (String)Amf0Value.getValueObject(ch);
		if(!"onMetaData".equals(tag)) {
			throw new Exception("先頭がonMetaDataになっていませんでした。");
		}
		System.out.println(Amf0Value.getValueObject(ch));
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void writeTag(WritableByteChannel target) throws Exception {
		// データ量を調べ直す必要あり。
		ByteBuffer titleBuffer = Amf0Value.getValueBuffer(title);
		ByteBuffer dataBuffer = Amf0Value.getValueBuffer(data);
		// データサイズを書き換えておく
		setSize(titleBuffer.remaining() + dataBuffer.remaining());
		// 頭の11バイト書き込み
		target.write(getHeaderBuffer((byte)0x12));
		// 実データ部書き込み
		// onMetaDataと書き込む
		target.write(titleBuffer);
		// 内容を書き込む
		target.write(dataBuffer);
		target.write(getTailBuffer());
	}
	@Override
	public String toString() {
		return "metaTag:" + getTimestamp();
	}
}
