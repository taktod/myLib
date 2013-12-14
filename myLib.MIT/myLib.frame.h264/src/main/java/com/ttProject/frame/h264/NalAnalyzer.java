package com.ttProject.frame.h264;

import java.nio.ByteBuffer;

import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.IAnalyzer;
import com.ttProject.unit.IUnit;
import com.ttProject.util.BufferUtil;

/**
 * mpegtsのような00 00 01 + dataのnalを解析する動作
 * 実体の読み込みまで実施します。
 * @author taktod
 */
public class NalAnalyzer implements IAnalyzer {
	/** selector */
	private H264FrameSelector selector = new H264FrameSelector();
	/**
	 * セレクター参照
	 * @return
	 */
	public H264FrameSelector getSelector() {
		return selector;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public IUnit analyze(IReadChannel channel) throws Exception {
		Short lastData = null;
		ByteBuffer buffer = ByteBuffer.allocate(channel.size() - channel.position());
		// データを読み込んでいく
		while(channel.size() - channel.position() > 1) {
			short data = BufferUtil.safeRead(channel, 2).getShort();
			// 00 00 00 01もしくは 00 00 01がnalの分岐点
			// よってshort = 0になった場合に注意して処理すればいい
			if(data == 0) {
				byte firstByte, secondByte;
				firstByte = BufferUtil.safeRead(channel, 1).get();
				if(firstByte == 1) {
					checkLastData(buffer, lastData);
					buffer.flip();
					if(buffer.remaining() == 0) {
						buffer = ByteBuffer.allocate(channel.size() - channel.position());
						continue;
					}
					return setupFrame(buffer);
				}
				else if(firstByte == 0) {
					secondByte = BufferUtil.safeRead(channel, 1).get();
					if(secondByte == 1) {
						checkLastData(buffer, lastData);
						buffer.flip();
						if(buffer.remaining() == 0) {
							buffer = ByteBuffer.allocate(channel.size() - channel.position());
							continue;
						}
						return setupFrame(buffer);
					}
					else {
						if(lastData != null) {
							buffer.putShort(lastData);
						}
						buffer.putShort(data);
						buffer.put(firstByte);
						buffer.put(secondByte);
					}
				}
				else {
					if(lastData != null) {
						buffer.putShort(lastData);
					}
					buffer.putShort(data);
					buffer.put(firstByte);
				}
				lastData = null;
			}
			else { // 0ではない
				if(lastData != null && data == 1 && (lastData & 0x00FF) == 0) {
					checkLastData(buffer, lastData);
					buffer.flip();
					if(buffer.remaining() == 0) {
						buffer = ByteBuffer.allocate(channel.size() - channel.position());
						continue;
					}
					return setupFrame(buffer);
				}
				setLastData(buffer, lastData);
				lastData = data;
			}
		}
		setLastData(buffer, lastData);
		if(channel.size() - channel.position() == 1) {
			buffer.put(BufferUtil.safeRead(channel, 1).get());
		}
		buffer.flip();
		if(buffer.remaining() == 0) {
			return null;
		}
		return setupFrame(buffer);
	}
	private IUnit setupFrame(ByteBuffer buffer) throws Exception {
		IReadChannel channel = new ByteReadChannel(buffer);
		IUnit unit = selector.select(channel);
		unit.load(channel);
		return unit;
	}
	private void setLastData(ByteBuffer buffer, Short lastData) {
		if(lastData != null) {
			buffer.putShort(lastData);
		}
	}
	private void checkLastData(ByteBuffer buffer, Short lastData) {
		if(lastData != null) {
			if((lastData & 0x00FF) == 0) {
				buffer.put((byte)(lastData >>> 8));
			}
			else {
				buffer.putShort(lastData);
			}
		}
	}
}
