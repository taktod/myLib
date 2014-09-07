/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.theora.type;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import com.ttProject.frame.theora.TheoraFrame;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit16;
import com.ttProject.unit.extra.bit.Bit2;
import com.ttProject.unit.extra.bit.Bit20;
import com.ttProject.unit.extra.bit.Bit24;
import com.ttProject.unit.extra.bit.Bit3;
import com.ttProject.unit.extra.bit.Bit32;
import com.ttProject.unit.extra.bit.Bit5;
import com.ttProject.unit.extra.bit.Bit6;
import com.ttProject.unit.extra.bit.Bit8;
import com.ttProject.util.BufferUtil;

/**
 * packetType 0x80
 * theoraString "theora"
 * theoraのidentificationHeaderDecodeFrame
 * @see http://www.theora.org/doc/Theora.pdf
 * @author taktod
 */
public class IdentificationHeaderDecodeFrame extends TheoraFrame {
	/** ロガー */
	private Logger logger = Logger.getLogger(IdentificationHeaderDecodeFrame.class);
	private Bit8  packetType = new Bit8();
	private String theoraString = "theora";
	private Bit8  Vmaj = new Bit8();
	private Bit8  Vmin = new Bit8();
	private Bit8  Vrev = new Bit8();
	private Bit16 FmbW = new Bit16();
	private Bit16 FmbH = new Bit16();
//	private Bit32 Nsbs = new Bit32();
//	private Bit36 Nbs = new Bit36();
//	private Bit32 Nmbs = new Bit32();
	private Bit20 PicW = new Bit20();
	private Bit20 PicH = new Bit20();
	private Bit8  PicX = new Bit8();
	private Bit8  PicY = new Bit8();
	private Bit32 Frn = new Bit32();
	private Bit32 Frd = new Bit32();
	private Bit24 ParN = new Bit24();
	private Bit24 ParD = new Bit24();
	private Bit8  Cs = new Bit8();
	private Bit24 Nombr = new Bit24();
	private Bit6  Qual = new Bit6();
	private Bit5  KfgShift = new Bit5();
	private Bit2  Pf = new Bit2();
	private Bit3  res = new Bit3();

	private CommentHeaderFrame commentHeaderFrame = null;
	private SetupHeaderFrame setupHeaderFrame = null;
	@Override
	public ByteBuffer getPackBuffer() throws Exception {
		return null;
	}
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		if(BufferUtil.safeRead(channel, 1).get() != (byte)0x80) {
			throw new Exception("先頭のheaderTypeのデータがおかしいです。");
		}
		packetType.set(0x80);
		String strBuffer = new String(BufferUtil.safeRead(channel, 6).array());
		if(!strBuffer.equals(theoraString)) {
			throw new Exception("theoraの文字列が一致しません。");
		}
		BitLoader loader = new BitLoader(channel);
//		loader.setLittleEndianFlg(true); // ここはlittleEndianではないっぽい
		loader.load(Vmaj, Vmin, Vrev, 
				FmbW, FmbH,
				PicW, PicH, PicX, PicY,
				Frn, Frd, ParN, ParD,
				Cs, Nombr, Qual, KfgShift, Pf, res);
		logger.info(FmbW.get() * 16 + "x" + FmbH.get() * 16);
		setWidth(FmbW.get() * 16);
		setHeight(FmbH.get() * 16);
		super.update();
	}
	@Override
	public void load(IReadChannel channel) throws Exception {
		; // 特にすることなし
		super.update();
	}
	@Override
	protected void requestUpdate() throws Exception {
		
	}
	/**
	 * CommentHeaderFrame設定
	 * @param frame
	 */
	public void setCommentHeaderFrame(CommentHeaderFrame frame) {
		commentHeaderFrame = frame;
	}
	/**
	 * SetupHeaderFrame設定
	 * @param frame
	 */
	public void setSetupHeaderFrame(SetupHeaderFrame frame) {
		setupHeaderFrame = frame;
	}
}
