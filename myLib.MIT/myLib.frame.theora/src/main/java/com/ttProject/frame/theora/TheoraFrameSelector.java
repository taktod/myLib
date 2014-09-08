/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.theora;

import org.apache.log4j.Logger;

import com.ttProject.frame.VideoSelector;
import com.ttProject.frame.theora.type.CommentHeaderFrame;
import com.ttProject.frame.theora.type.IdentificationHeaderDecodeFrame;
import com.ttProject.frame.theora.type.InterFrame;
import com.ttProject.frame.theora.type.IntraFrame;
import com.ttProject.frame.theora.type.SetupHeaderFrame;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.IUnit;
import com.ttProject.util.BufferUtil;

/**
 * theoraのフレーム解析を実施します。
 * @author taktod
 */
public class TheoraFrameSelector extends VideoSelector {
	/** ロガー */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(TheoraFrameSelector.class);
	// 先にこれらのデータを読み込む必要あり
	private IdentificationHeaderDecodeFrame identificationHeaderDecodeFrame = null;
	private CommentHeaderFrame commentHeaderFrame = null;
	private SetupHeaderFrame setupHeaderFrame = null;
	@Override
	public IUnit select(IReadChannel channel) throws Exception {
		if(channel.position() == channel.size()) {
			return null;
		}
		// 1byte目を参照してなにかするみたいな形にしておこう。
		byte firstByte = BufferUtil.safeRead(channel, 1).get();
		if((firstByte & 0x80) != 0x00) {
			if(identificationHeaderDecodeFrame != null
			&& commentHeaderFrame != null
			&& setupHeaderFrame != null) {
				// 本当はここ、上書きした方がいいかもしれないね
				throw new Exception("setupデータはすでに設定済みなのに設定項目をうけとりました。");
			}
		}
		else {
			if(identificationHeaderDecodeFrame == null
			|| commentHeaderFrame == null
			|| setupHeaderFrame == null) {
				// 本当はここ、上書きした方がいいかもしれないね
				throw new Exception("setupデータはすでに設定済みなのに設定項目をうけとりました。");
			}
		}
		// はじめの1bitが立っている→header
		// はじめの1bitが立っていない→通常のframe
		TheoraFrame frame = null;
		if(identificationHeaderDecodeFrame == null) {
			identificationHeaderDecodeFrame = new IdentificationHeaderDecodeFrame(firstByte);
			frame = identificationHeaderDecodeFrame;
		}
		else if(commentHeaderFrame == null) {
			commentHeaderFrame = new CommentHeaderFrame(firstByte);
			identificationHeaderDecodeFrame.setCommentHeaderFrame(commentHeaderFrame);
			frame = commentHeaderFrame;
		}
		else if(setupHeaderFrame == null) {
			setupHeaderFrame = new SetupHeaderFrame(firstByte);
			identificationHeaderDecodeFrame.setSetupHeaderFrame(setupHeaderFrame);
			frame = setupHeaderFrame;
		}
		else {
			if((firstByte & 0xC0) == 0x40) {
				frame = new IntraFrame(firstByte);
			}
			else {
				frame = new InterFrame(firstByte);
			}
			BufferUtil.quickDispose(channel, channel.size() - channel.position());
		}
		if(!(frame instanceof IdentificationHeaderDecodeFrame)) {
			frame.setIdentificationHeaderDecodeFrame(identificationHeaderDecodeFrame);
		}
		frame.minimumLoad(channel);
		return frame;
	}
}
