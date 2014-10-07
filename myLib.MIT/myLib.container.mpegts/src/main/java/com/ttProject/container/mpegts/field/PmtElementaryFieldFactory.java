/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.mpegts.field;

import com.ttProject.container.mpegts.MpegtsCodecType;
import com.ttProject.unit.extra.bit.Bit12;
import com.ttProject.unit.extra.bit.Bit13;
import com.ttProject.unit.extra.bit.Bit3;
import com.ttProject.unit.extra.bit.Bit4;
import com.ttProject.unit.extra.bit.Bit8;

/**
 * factory of elementaryField.(control field Pid.)
 * @author taktod
 */
public class PmtElementaryFieldFactory {
	/** next track pid */
	private short nextTrackPid = 0x0100;
	/** next audio streamId */
	private short nextAudioStreamId = 0xC0;
	/** next video streamId */
	private short nextVideoStreamId = 0xE0;
	/**
	 * make new field.
	 * @param codec
	 * @return
	 * @throws Exception
	 */
	public PmtElementaryField makeNewField(MpegtsCodecType codec) throws Exception {
		PmtElementaryField elementField = new PmtElementaryField(
				new Bit8(codec.intValue()), new Bit3(0x07), new Bit13(nextTrackPid ++), new Bit4(0x0F), new Bit12(0));
		switch(codec) {
		case AUDIO_AAC:
		case AUDIO_MPEG1:
			elementField.setSuggestStreamId(nextAudioStreamId ++);
			break;
		case VIDEO_H264:
			elementField.setSuggestStreamId(nextVideoStreamId ++);
			break;
		default:
			throw new Exception("unknown codecType found. I need sample.");
		}
		return elementField;
	}
}
