package com.ttProject.container.mp4;

import org.apache.log4j.Logger;

import com.ttProject.container.mp4.type.Edts;
import com.ttProject.container.mp4.type.Elst;
import com.ttProject.container.mp4.type.Free;
import com.ttProject.container.mp4.type.Ftyp;
import com.ttProject.container.mp4.type.Hdlr;
import com.ttProject.container.mp4.type.Mdat;
import com.ttProject.container.mp4.type.Mdhd;
import com.ttProject.container.mp4.type.Mdia;
import com.ttProject.container.mp4.type.Minf;
import com.ttProject.container.mp4.type.Moov;
import com.ttProject.container.mp4.type.Mvhd;
import com.ttProject.container.mp4.type.Tkhd;
import com.ttProject.container.mp4.type.Trak;
import com.ttProject.container.mp4.type.Udta;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.ISelector;
import com.ttProject.unit.IUnit;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit32;

/**
 * mp4のatomを解析していくselector
 * mp4Boxを取り出す動作なので、これをいくら進めてもframeは取り出せません。
 * stcoとか、sttsとかとmdatを絡めてやっとframeが抜き出せるようになる感じ。
 * @author taktod
 */
public class Mp4AtomSelector implements ISelector {
	/** ロガー */
	private Logger logger = Logger.getLogger(Mp4AtomSelector.class);
	@Override
	public IUnit select(IReadChannel channel) throws Exception {
		if(channel.position() == channel.size()) {
			return null;
		}
		// sourceから4byte読み込んで、長さとtagを取得する必要あり。
		Bit32 size = new Bit32();
		Bit32 name = new Bit32();
		BitLoader loader = new BitLoader(channel);
		loader.load(size, name);
		Mp4Atom atom = null;
		logger.info(Type.getType(name.get()));
		switch(Type.getType(name.get())) {
		case Ftyp:
			atom = new Ftyp(size, name);
			break;
		case Free:
			atom = new Free(size, name);
			break;
		case Mdat:
			atom = new Mdat(size, name);
			break;
		case Moov:
			atom = new Moov(size, name);
			break;
		case Mvhd:
			atom = new Mvhd(size, name);
			break;
		case Trak:
			atom = new Trak(size, name);
			break;
		case Udta:
			atom = new Udta(size, name);
			break;
		case Tkhd:
			atom = new Tkhd(size, name);
			break;
		case Edts:
			atom = new Edts(size, name);
			break;
		case Elst:
			atom = new Elst(size, name);
			break;
		case Mdia:
			atom = new Mdia(size, name);
			break;
		case Mdhd:
			atom = new Mdhd(size, name);
			break;
		case Hdlr:
			atom = new Hdlr(size, name);
			break;
		case Minf:
			atom = new Minf(size, name);
			break;
		default:
			return null;
		}
		atom.minimumLoad(channel);
		return atom;
	}
}
