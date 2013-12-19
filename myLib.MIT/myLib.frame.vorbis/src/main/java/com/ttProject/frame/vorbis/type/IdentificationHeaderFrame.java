package com.ttProject.frame.vorbis.type;

import java.nio.ByteBuffer;

import com.ttProject.frame.vorbis.VorbisFrame;
import com.ttProject.nio.channels.IReadChannel;

/**
 * vorbisのheaderフレーム
 * packetType: 1byte 0x01 identification header
 * string: 6Byte "vorbis"
 * vorbisVersion 32bit integer
 * audioChannels 8bit unsignedInteger
 * audioSampleRate 32bit integer
 * bitrateMaximum 32bit integer
 * bitrateNominal 32bit integer
 * bitrateMinimum 32bit integer
 * blockSize0 2^x 4bit unsigned integer(samples per frameがとれるっぽい)
 * blockSize1 2^x 4bit unsigned integer(不明)
 * framing flag 1bit(実際は1byteになってるっぽい)
 * 
 * @see http://www.xiph.org/vorbis/doc/Vorbis_I_spec.html#x1-620004.2.2
 * @author taktod
 */
public class IdentificationHeaderFrame extends VorbisFrame {

	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void load(IReadChannel channel) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void requestUpdate() throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public ByteBuffer getPackBuffer() {
		return null;
	}
}
