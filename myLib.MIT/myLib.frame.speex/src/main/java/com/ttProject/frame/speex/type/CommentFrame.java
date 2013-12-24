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
 */
public class CommentFrame extends SpeexFrame {
	/** ロガー */
	private Logger logger = Logger.getLogger(CommentFrame.class);
	/** venderName */
	private String venderName;
	/** データ要素 */
	private List<String> elementList = new ArrayList<String>();
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(IReadChannel channel) throws Exception {
		// ここでデータを読み込んで処理する。
		ByteBuffer buffer = BufferUtil.safeRead(channel, 4);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		int venderLength = buffer.getInt();
		venderName = new String(BufferUtil.safeRead(channel, venderLength).array());
		buffer = BufferUtil.safeRead(channel, 4);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		int elementCount = buffer.getInt();
		for(int i = 0;i < elementCount;i ++) {
			buffer = BufferUtil.safeRead(channel, 4);
			buffer.order(ByteOrder.LITTLE_ENDIAN);
			int elementLength = buffer.getInt();
			elementList.add(new String(BufferUtil.safeRead(channel, elementLength).array()));
		}
		logger.info(venderName);
		logger.info(elementList);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
		// 結合は特に問題ないので、あとでつくっておくことにします。
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ByteBuffer getPackBuffer() {
		return null;
	}
}
