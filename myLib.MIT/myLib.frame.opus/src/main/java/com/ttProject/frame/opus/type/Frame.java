package com.ttProject.frame.opus.type;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import com.ttProject.frame.opus.OpusFrame;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.util.BufferUtil;

/**
 * opusのフレーム
 * @author taktod
 *
 */
public class Frame extends OpusFrame {
	/** ロガー */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(Frame.class);
	/** frameの内部データ */
	private ByteBuffer frameBuffer = null;
	private byte firstByte;
	/**
	 * コンストラクタ
	 * @param firstByte
	 */
	public Frame(byte firstByte) {
		this.firstByte = firstByte;
	}
	/**
	 * コンストラクタ
	 */
	public Frame() {
	}
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		
	}

	@Override
	public void load(IReadChannel channel) throws Exception {
		super.setSize(channel.size());
		ByteBuffer buffer = ByteBuffer.allocate(channel.size());
		buffer.put(firstByte);
		buffer.put(BufferUtil.safeRead(channel, channel.size() - 1));
		buffer.flip();
		super.setData(buffer);
		super.update();
	}
	@Override
	protected void requestUpdate() throws Exception {
		if(frameBuffer == null) {
			throw new Exception("frameBufferがnullでした、先に解析動作を実施してください。");
		}
		super.setData(frameBuffer);
	}
	@Override
	public ByteBuffer getPackBuffer() throws Exception {
//		return getData();
		throw new Exception("OpusのpackBufferは不明です。");
	}
	@Override
	public boolean isComplete() {
		return true;
	}

}
