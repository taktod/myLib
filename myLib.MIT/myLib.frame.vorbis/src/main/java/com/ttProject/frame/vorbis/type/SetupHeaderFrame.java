package com.ttProject.frame.vorbis.type;

import com.ttProject.frame.vorbis.VorbisFrame;
import com.ttProject.nio.channels.IReadChannel;

/**
 * vorbisのheaderフレーム
 * packetType: 1byte 0x05 setup header
 * string: 6Byte "vorbis"
 * あとのデータはよくわからん。
 * 
 * @see http://www.xiph.org/vorbis/doc/Vorbis_I_spec.html#x1-620004.2.2
 * @author taktod
 */
public class SetupHeaderFrame extends VorbisFrame {

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
	
}
