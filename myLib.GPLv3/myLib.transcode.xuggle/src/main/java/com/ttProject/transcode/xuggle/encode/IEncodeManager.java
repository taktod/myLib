package com.ttProject.transcode.xuggle.encode;

import com.ttProject.transcode.ITranscodeListener;
import com.ttProject.transcode.xuggle.packet.IDepacketizer;
import com.xuggle.xuggler.IStreamCoder;

public interface IEncodeManager {
	public void setTranscodeListener(ITranscodeListener listener);
	public void close();
	public void setEncoder(IStreamCoder encoder) throws Exception;
	public void setDepacketizer(IDepacketizer depacketizer);
	public void encode(Object xuggleObject) throws Exception;
}
