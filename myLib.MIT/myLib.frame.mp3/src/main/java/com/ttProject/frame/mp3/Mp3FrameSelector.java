package com.ttProject.frame.mp3;

import com.ttProject.frame.mp3.type.Frame;
import com.ttProject.frame.mp3.type.ID3Frame;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.ISelector;
import com.ttProject.unit.IUnit;
import com.ttProject.unit.extra.Bit8;
import com.ttProject.unit.extra.BitLoader;

/**
 * mp3のframeを選択するプログラム
 * @author taktod
 */
public class Mp3FrameSelector implements ISelector {
	/**
	 * {@inheritDoc}
	 */
	@Override
	public IUnit select(IReadChannel channel) throws Exception {
		if(channel.size() - channel.position() < 1) {
			// 少なくとも1バイトは必要
			return null;
		}
		Bit8 firstByte = new Bit8();
		BitLoader loader = new BitLoader(channel);
		loader.load(firstByte);
		Mp3Frame frame = null;
		switch(firstByte.get()) {
		case 'I': // ID3?
			frame = new ID3Frame();
			break;
		case 'T': // TAG?
			break;
		case 0xFF: // Frame
			frame = new Frame();
			break;
		default:
			throw new Exception("解析不能なデータです");
		}
		frame.minimumLoad(channel);
		return frame;
	}
}
