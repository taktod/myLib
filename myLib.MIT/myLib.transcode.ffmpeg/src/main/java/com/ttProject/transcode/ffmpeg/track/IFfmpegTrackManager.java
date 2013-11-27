package com.ttProject.transcode.ffmpeg.track;

import com.ttProject.transcode.ITrackManager;

/**
 * ffmpegの変換用のtrackManager
 * @author taktod
 */
public interface IFfmpegTrackManager extends ITrackManager {
	/**
	 * unitを取捨選択する動作
	 * @param selector
	 */
	public void setUnitSelector(IUnitSelector selector);
}
