package com.ttProject.transcode.ffmpeg.track;

import com.ttProject.media.Unit;
import com.ttProject.media.flv.tag.VideoTag;

/**
 * 
 * @author taktod
 *
 */
public class FlvVideoUnitSelector implements IUnitSelector {
	@Override
	public boolean check(Unit unit) {
		return unit instanceof VideoTag;
	}
}
