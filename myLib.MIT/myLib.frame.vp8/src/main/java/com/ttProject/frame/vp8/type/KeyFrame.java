package com.ttProject.frame.vp8.type;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import com.ttProject.frame.vp8.Vp8Frame;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.BitConnector;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit14;
import com.ttProject.unit.extra.bit.Bit19;
import com.ttProject.unit.extra.bit.Bit2;
import com.ttProject.unit.extra.bit.Bit3;
import com.ttProject.unit.extra.bit.Bit8;
import com.ttProject.util.BufferUtil;

/**
 * vp8のkeyFrame
 * @author taktod
 */
public class KeyFrame extends Vp8Frame {
	/** ロガー */
	private Logger logger = Logger.getLogger(KeyFrame.class);
	public static byte[] startCode = {(byte)0x9D, (byte)0x01, (byte)0x2A};
	private Bit14 width           = new Bit14();
	private Bit2  horizontalScale = new Bit2();
	private Bit14 height          = new Bit14();
	private Bit2  verticalScale   = new Bit2();
	private ByteBuffer buffer;
	public KeyFrame(Bit1 frameType, Bit3 version, Bit1 showFrame, Bit19 firstPartSize) {
		super(frameType, version, showFrame, firstPartSize);
		super.update();
	}
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		// ここでサイズのデータを読み込みたい
		ByteBuffer buffer = BufferUtil.safeRead(channel, 3);
		if(buffer.get() != startCode[0]
		|| buffer.get() != startCode[1]
		|| buffer.get() != startCode[2]) {
			logger.info("keyFrameのstartCodeがおかしいです。");
		}
		BitLoader loader = new BitLoader(channel);
		loader.setLittleEndianFlg(true);
		loader.load(width, horizontalScale, height, verticalScale);
		setSize(channel.size());
		setWidth(width.get());
		setHeight(height.get());
		setReadPosition(channel.position());
		super.update();
	}
	@Override
	public void load(IReadChannel channel) throws Exception {
		channel.position(getReadPosition());
		buffer = BufferUtil.safeRead(channel, getSize() - getReadPosition());
		super.update();
	}
	@Override
	protected void requestUpdate() throws Exception {
		if(buffer == null) {
			throw new Exception("本体データが設定されていません");
		}
		BitConnector connector = new BitConnector();
		connector.setLittleEndianFlg(true);
		setData(BufferUtil.connect(getHeaderBuffer(),
				connector.connect(new Bit8(startCode[0]), new Bit8(startCode[1]), new Bit8(startCode[2]), width, horizontalScale, height, verticalScale),
				buffer));
	}
	@Override
	public ByteBuffer getPackBuffer() throws Exception {
		return getData();
	}
}
