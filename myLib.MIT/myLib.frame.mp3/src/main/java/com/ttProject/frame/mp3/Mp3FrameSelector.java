package com.ttProject.frame.mp3;

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
	@Override
	public IUnit select(IReadChannel channel) throws Exception {
		if(channel.size() - channel.position() < 1) {
			// 少なくとも1バイトは必要
			return null;
		}
		Bit8 firstByte = new Bit8();
		BitLoader loader = new BitLoader(channel);
		loader.load(firstByte);
		switch(firstByte.get()) {
		case 'I': // ID3?
			break;
		case 'T': // TAG?
			break;
		case 0xFF: // Frame
			break;
		default:
			throw new Exception("解析不能なデータです");
		}
		return null;
	}
}
