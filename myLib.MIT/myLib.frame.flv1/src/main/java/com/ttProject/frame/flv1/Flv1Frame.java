package com.ttProject.frame.flv1;

import java.nio.ByteBuffer;

import com.ttProject.frame.IVideoFrame;
import com.ttProject.frame.VideoFrame;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.Bit;
import com.ttProject.unit.extra.Bit1;
import com.ttProject.unit.extra.Bit2;
import com.ttProject.unit.extra.Bit3;
import com.ttProject.unit.extra.Bit5;
import com.ttProject.unit.extra.Bit8;
import com.ttProject.unit.extra.BitConnector;
import com.ttProject.unit.extra.BitN.Bit17;
import com.ttProject.util.BufferUtil;

/**
 * flv1のframeのベース
 * @see http://hkpr.info/flash/swf/index.php?%E3%83%93%E3%83%87%E3%82%AA%2FSorenson%20H.263%20%E3%83%93%E3%83%83%E3%83%88%E3%82%B9%E3%83%88%E3%83%AA%E3%83%BC%E3%83%A0%E3%83%95%E3%82%A9%E3%83%BC%E3%83%9E%E3%83%83%E3%83%88
 * @author taktod
 */
public abstract class Flv1Frame extends VideoFrame implements IVideoFrame {
	private Bit17 pictureStartCode;
	private Bit5 version;
	private Bit8 temporalReference;
	private Bit3 pictureSize;
	private Bit customWidth;
	private Bit customHeight;
	private Bit2 pictureType;
	private Bit1 deblockingFlag;
	private Bit5 quantizer;
	private Bit1 extraInformationFlag;
	private Bit8 extraInformation;
	private Bit extra; // 帳尻あわせ用
	private ByteBuffer buffer = null; // あとで読み込みさせたい場合にいれておく

	public Flv1Frame(Bit17 pictureStartCode,
			Bit5 version, Bit8 temporalReference, Bit3 pictureSize,
			Bit customWidth, Bit customHeight,
			int width, int height, Bit2 pictureType, Bit1 deblockingFlag,
			Bit5 quantizer, Bit1 extraInformationFlag, Bit8 extraInformation, Bit extra) {
		this.pictureStartCode = pictureStartCode;
		this.version = version;
		this.temporalReference = temporalReference;
		this.pictureSize = pictureSize;
		this.customWidth = customWidth;
		this.customHeight = customHeight;
		this.pictureType = pictureType;
		this.deblockingFlag = deblockingFlag;
		this.quantizer = quantizer;
		this.extraInformationFlag = extraInformationFlag;
		this.extraInformation = extraInformation;
		this.extra = extra;
		setWidth(width);
		setHeight(height);
		setDts(0);
	}
	/**
	 * 全体のデータを応答します。
	 */
	@Override
	public ByteBuffer getData() throws Exception {
		if(buffer == null) {
			throw new Exception("本体データが設定されていません。");
		}
		// まず保持しているbitデータを結合します。
		BitConnector bitConnector = new BitConnector();
		ByteBuffer bitData = bitConnector.connect(pictureStartCode, version, temporalReference,
				pictureSize, customWidth, customHeight,
				pictureType, deblockingFlag, quantizer,
				extraInformationFlag, extraInformation, extra);
		int size = bitData.remaining() + buffer.remaining();
		ByteBuffer data = ByteBuffer.allocate(size);
		data.put(bitData);
		data.put(buffer.duplicate());
		data.flip();
		return data;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(IReadChannel channel) throws Exception {
		// detail読み込みでは、必要なデータを読み込みます。
		channel.position(getReadPosition());
		buffer = BufferUtil.safeRead(channel, getSize() - getReadPosition());
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		// 小規模読み込みの動作は特にすることなし。
		setReadPosition(channel.position());
		super.setSize(channel.size());
		super.update();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
		if(buffer == null) {
			throw new Exception("本体データが設定されていません。");
		}
		BitConnector connector = new BitConnector();
		setData(BufferUtil.connect(connector.connect(pictureStartCode, version, temporalReference,
				pictureSize, customWidth, customHeight,
				pictureType, deblockingFlag, quantizer,
				extraInformationFlag, extraInformation, extra),
				buffer));
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setPts(long pts) {
		super.setPts(pts);
	}
}