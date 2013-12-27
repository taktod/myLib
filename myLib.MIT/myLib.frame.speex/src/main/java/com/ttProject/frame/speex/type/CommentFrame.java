package com.ttProject.frame.speex.type;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ttProject.frame.speex.SpeexFrame;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.util.BufferUtil;

/**
 * speexのCommentFrame(metaデータみたいなものかな)
 * 
 * 4byte int venderLength
 * nbyte string venderName
 * 4byte int elementNum
 *  4byte elementLength
 *  nbyte elementString
 *  をelementNum分繰り返す
 * @author taktod
 * TODO こちらの動作ですが、解析途上の場合は、フラグで確認できるようにして、再度loadし直したら続きから処理できるようにしたいところ。
 */
public class CommentFrame extends SpeexFrame {
	/** ロガー */
	private Logger logger = Logger.getLogger(CommentFrame.class);
	/** venderName */
	private String venderName = null;
	/** データサイズ */
	private Integer elementSize = null;
	/** データ要素 */
	private List<String> elementList = new ArrayList<String>();
	/** 読み込み途上のデータバッファ */
	private ByteBuffer tmpBuffer = null;
	/**
	 * コンストラクタ
	 */
	public CommentFrame() {
		super.update();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		super.update();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(IReadChannel channel) throws Exception {
		// tmpBufferにデータがある場合は結合しなければならない。
		if(tmpBuffer != null) {
			
		}
		if(venderName == null) {
			// ここでデータを読み込んで処理する。
			ByteBuffer buffer = BufferUtil.safeRead(channel, 4);
			buffer.order(ByteOrder.LITTLE_ENDIAN);
			// データの読み込みを試す。足りない場合は、データを保持しておいて次にまわす必要がある。
			// venderLength分データを取得したい。
			int venderLength = buffer.getInt();
			// データが必要分あるか確認
			if(channel.size() - channel.position() < venderLength) {
				// 次回に持ち越し
				// 必要データはtmpBufferに記録しておく。
				tmpBuffer = ByteBuffer.allocate(channel.size() - channel.position() + 4);
				tmpBuffer.order(ByteOrder.LITTLE_ENDIAN);
				tmpBuffer.putInt(venderLength);
				tmpBuffer.put(BufferUtil.safeRead(channel, channel.size() - channel.position()));
				return;
			}
			venderName = new String(BufferUtil.safeRead(channel, venderLength).array());
			logger.info("venderName:" + venderName);
		}
		if(elementSize == null) {
			// 問題はこのあと。
			ByteBuffer buffer = BufferUtil.safeRead(channel, 4);
			buffer.order(ByteOrder.LITTLE_ENDIAN);
			elementSize = buffer.getInt();
			logger.info("elementCount:" + elementSize);
		}
		for(int i = 0;i < elementSize;i ++) {
			// 長さを読み込んで必要なデータを取り出す。
			ByteBuffer buffer = BufferUtil.safeRead(channel, 4);
			buffer.order(ByteOrder.LITTLE_ENDIAN);
			int elementLength = buffer.getInt();
			elementList.add(new String(BufferUtil.safeRead(channel, elementLength).array()));
		}
		logger.info(venderName);
		logger.info(elementList);
		super.update();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
		if(venderName == null) {
			venderName = "myLib.speex.muxer";
		}
		// venderLength
		// venderData
		// elementListSize
		//  elementDataLength
		//  elementData
		// を繰り返す
		int size = 4 + venderName.length() + 4;
		for(String element : elementList) {
			size += 4 + element.length();
		}
		ByteBuffer buffer = ByteBuffer.allocate(size);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		buffer.putInt(venderName.length());
		buffer.put(venderName.getBytes());
		buffer.putInt(elementList.size());
		for(String element : elementList) {
			buffer.putInt(element.length());
			buffer.put(element.getBytes());
		}
		buffer.flip();
		setData(buffer);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ByteBuffer getPackBuffer() {
		return null;
	}
	public void setVenderName(String name) {
		this.venderName = name;
		super.update();
	}
	public void addElement(String data) {
		elementList.add(data);
		super.update();
	}
	public void removeElement(String data) {
		elementList.remove(data);
		super.update();
	}
	@Override
	public boolean isComplete() {
		return venderName != null && elementSize != null && elementSize == elementList.size();
	}
}
