package com.ttProject.frame.vorbis.type;

import java.nio.ByteBuffer;

import com.ttProject.frame.vorbis.VorbisFrame;
import com.ttProject.nio.channels.IReadChannel;

/**
 * vorbisのheaderフレーム
 * packetType: 1byte 0x03 comment header
 * string: 6Byte "vorbis"
 * venderLength: 4byte integer
 * venderString: nbyte
 * [繰り返す]
 * iterateNum: 4byte integer
 * length: 4byte integer
 * string: nbyte (utfっぽい)
 * [繰り返すここまで]
 * framing flag 1bit(実際は1byteになってるっぽい)
 * 
 * @see http://www.xiph.org/vorbis/doc/Vorbis_I_spec.html#x1-620004.2.2
 * @author taktod
 */
public class CommentHeaderFrame extends VorbisFrame {

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
