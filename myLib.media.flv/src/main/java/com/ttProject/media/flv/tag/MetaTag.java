package com.ttProject.media.flv.tag;

import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.ttProject.media.flv.Tag;
import com.ttProject.media.flv.amf.Amf0Value;
import com.ttProject.nio.channels.IFileReadChannel;
import com.ttProject.util.BufferUtil;

/**
 * medaデータ
 * 12 xx xx xx tt tt tt tt 00 00 00 [AMF0でonMetaData(文字列)] [AMF0でObjectMapデータ] xx xx xx xx
 * xxの部分はサイズ、先頭のサイズと終端のサイズと２つある。
 * ttの部分はtimestamp
 * 
 * @author taktod
 */
public class MetaTag extends Tag {
	/** メタデータの基本文字列 */
	private final String title = "onMetaData";
	/** メタデータの中身 */
	private final Map<String, Object> data = new LinkedHashMap<String, Object>();
	/** メタデータの実データ */
	private ByteBuffer rawData = null;
	/**
	 * データの設定
	 * @param key
	 * @param data
	 */
	public void putData(String key, Object data) {
		rawData = null;
		// 追加したら、すでに計算済みのrawDataを破棄して、あたらしいデータを追加しておきます。
		this.data.put(key, data);
	}
	/**
	 * データの削除
	 * @param key
	 */
	public void removeData(String key) {
		rawData = null;
		this.data.remove(key);
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
	 * コンストラクタ(メモリーからつくられる場合)
	 */
	public MetaTag() {
		super();
	}
	/**
	 * コンストラクタ(ファイルベースの場合・・・だがMetaTagとしては、デフォルトでデータを読み込んでdataをつくっておきたいところ・・・)
	 * @param size
	 * @param position
	 * @param timestamp
	 */
	public MetaTag(final int position, final int size, final int timestamp) {
		super(position, size, timestamp);
	}
	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void analyze(IFileReadChannel ch, boolean atBegin) throws Exception {
		super.analyze(ch, atBegin);
		// 実データを読み込む
		ch.position(getPosition() + 11);
		// データを読み込む
		String tag = (String)Amf0Value.getValueObject(ch);
		if(!"onMetaData".equals(tag)) {
			throw new Exception("先頭がonMetaDataになっていませんでした。");
		}
		while(ch.position() < getPosition() + super.getSize() - 4) { // tailの部分があるので4バイト引いておく。
			Object data = Amf0Value.getValueObject(ch);
			if(!(data instanceof Map<?, ?>)) {
				throw new Exception("内部データの構成がMapではありませんでした。");
			}
			Map<String, Object> object = (Map<String, Object>)data;
			for(Entry<String, Object> entry : object.entrySet()) {
				this.data.put(entry.getKey(), entry.getValue());
			}
		}
		// tailについて確認しておく。
		if(BufferUtil.safeRead(ch, 4).getInt() != super.getSize() - 4) {
			throw new Exception("tailByteの長さが狂ってます");
		}
		
		rawData = null;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void writeTag(WritableByteChannel target) throws Exception {
		if(rawData == null) {
			getSize();
		}
		// 頭の11バイト書き込み
		target.write(getHeaderBuffer((byte)0x12));
		// 実データ部書き込みdata
		// 内容を書き込む
		rawData.position(0);
		target.write(rawData);
		target.write(getTailBuffer());
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getSize() {
		// 実データbufferを作成して計算しなおしておく。
		if(rawData == null) {
			System.out.println("サイズを計算します。");
			try {
				// onMetaDataの文字列の部分
				ByteBuffer titleBuffer = Amf0Value.getValueBuffer(title);
				ByteBuffer dataBuffer = Amf0Value.getValueBuffer(data);
				rawData = ByteBuffer.allocate(titleBuffer.remaining() + dataBuffer.remaining());
				rawData.put(titleBuffer);
				rawData.put(dataBuffer);
				rawData.flip();
				super.setSize(rawData.remaining() + 15);
			}
			catch (Exception e) {
				throw new RuntimeException("不明な例外が発生しました。");
			}
		}
		return super.getSize();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ByteBuffer getBuffer() throws Exception {
		// 全体のデータサイズを知っておく必要がある。
		ByteBuffer buffer = ByteBuffer.allocate(getSize());
		buffer.put(getHeaderBuffer((byte)0x12));
		rawData.position(0);
		buffer.put(rawData);
		buffer.put(getTailBuffer());
		buffer.flip();
		return buffer;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "metaTag ts:" + getTimestamp() + " pos:" + getPosition() + " sz:" + getSize();
	}
}
