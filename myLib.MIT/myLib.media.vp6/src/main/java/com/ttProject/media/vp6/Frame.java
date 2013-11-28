package com.ttProject.media.vp6;

import java.nio.ByteBuffer;

import com.ttProject.media.IAnalyzer;
import com.ttProject.media.IVideoData;
import com.ttProject.media.Unit;
import com.ttProject.media.extra.Bit1;
import com.ttProject.media.extra.Bit2;
import com.ttProject.media.extra.Bit5;
import com.ttProject.media.extra.Bit6;
import com.ttProject.media.extra.Bit8;
import com.ttProject.nio.channels.IReadChannel;

/**
 * on2Vp6のコーデックの映像の内容を解析します。
 * @see http://wiki.multimedia.cx/index.php?title=On2_VP6
 * 00 78 46 0F 14 0F 14 3F 6E E8 CB 01 8D C9 89 26 9E AD 53 6F 33 FD DD F2 BF AB F6 ED FB 1C
 * flvにはいっているvp6のデータの先頭は、終端にくる必要があるらしい。
 * よって解析データは78から・・・となります。
 * @author taktod
 */
public class Frame extends Unit implements IVideoData {
	private Bit1 frameMode;
	private Bit6 qp;
	private Bit1 marker;
	
	private Bit5 version;
	private Bit2 version2;
	private Bit1 interlace;
	
	private short offset; // 16bit
	
	private Bit8 dimY; // x16で縦幅
	private Bit8 dimX; // x16で横幅
	private Bit8 renderY; // x16で縦幅
	private Bit8 renderX; // x16で横幅
	/**
	 * コンストラクタ
	 */
	public Frame() {
		super(0, 0);
	}
	@Override
	public long getPts() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getDts() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getTimebase() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ByteBuffer getRawData() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void analyze(IReadChannel ch, IAnalyzer<?> analyzer)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

}
