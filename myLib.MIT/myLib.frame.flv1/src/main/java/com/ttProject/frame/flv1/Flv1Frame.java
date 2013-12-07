package com.ttProject.frame.flv1;

import java.nio.ByteBuffer;

import com.ttProject.frame.IVideoFrame;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.Bit;
import com.ttProject.unit.extra.Bit1;
import com.ttProject.unit.extra.Bit2;
import com.ttProject.unit.extra.Bit3;
import com.ttProject.unit.extra.Bit5;
import com.ttProject.unit.extra.Bit8;
import com.ttProject.unit.extra.BitConnector;
import com.ttProject.unit.extra.BitN.Bit17;

/**
 * flv1のframeのベース
 * @see http://hkpr.info/flash/swf/index.php?%E3%83%93%E3%83%87%E3%82%AA%2FSorenson%20H.263%20%E3%83%93%E3%83%83%E3%83%88%E3%82%B9%E3%83%88%E3%83%AA%E3%83%BC%E3%83%A0%E3%83%95%E3%82%A9%E3%83%BC%E3%83%9E%E3%83%83%E3%83%88
 * @author taktod
 */
public abstract class Flv1Frame implements IVideoFrame {
	private Bit17 pictureStartCode;
	private Bit5 version;
	private Bit8 temporalReference;
	private Bit3 pictureSize;
	private Bit customWidth;
	private Bit customHeight;
	private int width;
	private int height;
	private Bit2 pictureType;
	private Bit1 deblockingFlag;
	private Bit5 quantizer;
	private Bit1 extraInformationFlag;
	private Bit8 extraInformation;
	private Bit extra; // 帳尻あわせ用
	private ByteBuffer buffer = null; // あとで読み込みさせたい場合にいれておく

	private int size = -1;
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
		this.width = width;
		this.height = height;
		this.pictureType = pictureType;
		this.deblockingFlag = deblockingFlag;
		this.quantizer = quantizer;
		this.extraInformationFlag = extraInformationFlag;
		this.extraInformation = extraInformation;
		this.extra = extra;
	}
	public Bit5 getVersion() {
		return version;
	}
	public Bit8 getTemporalReference() {
		return temporalReference;
	}
	public int getWidth() {
		return width;
	}
	public int getHeight() {
		return height;
	}
	public Bit2 getPictureType() {
		return pictureType;
	}
	public Bit1 getDeblockingFlag() {
		return deblockingFlag;
	}
	public Bit5 getQuantizer() {
		return quantizer;
	}
	public Bit1 getExtraInformationFlag() {
		return extraInformationFlag;
	}
	public Bit8 getExtraInformation() {
		return extraInformation;
	}
	@Override
	public long getPts() {
		return 0;
	}
	@Override
	public long getDts() {
		return 0;
	}
	@Override
	public long getTimebase() {
		return 0;
	}
	/**
	 * 残りデータを登録しておく
	 */
	public void setRemainData(ByteBuffer buffer) {
		this.buffer = buffer;
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
	 * データサイズを設定します。
	 * @param size
	 */
	public void setSize(int size) {
		this.size = size;
	}
	/**
	 * サイズを応答する
	 */
	@Override
	public int getSize() {
		return size;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(IReadChannel channel) throws Exception {
		
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		
	}
}
