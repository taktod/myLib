/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.transcode.ffmpeg.unit;

import java.nio.ByteBuffer;

import com.ttProject.media.Unit;
import com.ttProject.media.flv.FlvHeader;
import com.ttProject.media.flv.Tag;
import com.ttProject.transcode.exception.FormatChangeException;

/**
 * flvのunitをfilestreamに変換する動作
 * @author taktod
 */
public class FlvDeunitizer implements IDeunitizer {
	private boolean flvHeaderSentFlag = false;
	/**
	 * 入力unitが動作対象であるか確認
	 */
	@Override
	public boolean check(Unit unit) throws FormatChangeException {
		if(unit instanceof Tag) {
			return true;
		}
		return false;
	}
	/**
	 * flvのbufferに変換して応答
	 */
	@Override
	public ByteBuffer getBuffer(Unit unit) throws Exception {
		if(!(unit instanceof Tag)) {
			return null;
		}
		Tag tag = (Tag) unit;
		if(!flvHeaderSentFlag) {
			FlvHeader flvHeader = new FlvHeader();
			flvHeader.setAudioFlg(true);
			flvHeader.setVideoFlg(true);
			ByteBuffer buffer = flvHeader.getBuffer();
			flvHeaderSentFlag = true;
			ByteBuffer tagBuffer = tag.getBuffer();
			ByteBuffer result = ByteBuffer.allocate(buffer.remaining() + tagBuffer.remaining());
			result.put(buffer);
			result.put(tagBuffer);
			result.flip();
			return result;
		}
		else {
			return tag.getBuffer();
		}
	}
	@Override
	public void close() {
		
	}
}
