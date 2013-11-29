package com.ttProject.transcode.ffmpeg.unit;

import java.nio.ByteBuffer;
import java.util.List;

import com.ttProject.media.flv.FlvHeader;
import com.ttProject.media.flv.FlvManager;
import com.ttProject.nio.channels.ByteReadChannel;

/**
 * flvのbufferStreamをunitに変換するプログラム
 * @author taktod
 */
public class FlvUnitizer implements IUnitizer {
	private FlvHeader flvHeader = null;
	private final FlvManager flvManager = new FlvManager();
	/**
	 * 入力データをflvTagに書き換えます。
	 */
	@Override
	public List<?> getUnits(ByteBuffer buffer) throws Exception {
		if(flvHeader == null) {
			// 初アクセスの場合はflvHeaderであることを期待します。
			flvHeader = new FlvHeader();
			ByteReadChannel channel = new ByteReadChannel(buffer);
			flvHeader.analyze(channel);
			buffer.position(13);
		}
		return 	flvManager.getUnits(buffer);
	}
	@Override
	public void close() {
		
	}
}
