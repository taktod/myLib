package com.ttProject.container.mp4;

import org.apache.log4j.Logger;

import com.ttProject.container.mp4.type.Co64;
import com.ttProject.container.mp4.type.Ctts;
import com.ttProject.container.mp4.type.Dinf;
import com.ttProject.container.mp4.type.Dref;
import com.ttProject.container.mp4.type.Edts;
import com.ttProject.container.mp4.type.Elst;
import com.ttProject.container.mp4.type.Free;
import com.ttProject.container.mp4.type.Ftyp;
import com.ttProject.container.mp4.type.Hdlr;
import com.ttProject.container.mp4.type.Hmhd;
import com.ttProject.container.mp4.type.Iods;
import com.ttProject.container.mp4.type.Mdat;
import com.ttProject.container.mp4.type.Mdhd;
import com.ttProject.container.mp4.type.Mdia;
import com.ttProject.container.mp4.type.Mfhd;
import com.ttProject.container.mp4.type.Mfra;
import com.ttProject.container.mp4.type.Mfro;
import com.ttProject.container.mp4.type.Minf;
import com.ttProject.container.mp4.type.Moof;
import com.ttProject.container.mp4.type.Moov;
import com.ttProject.container.mp4.type.Mvhd;
import com.ttProject.container.mp4.type.Nmhd;
import com.ttProject.container.mp4.type.Skip;
import com.ttProject.container.mp4.type.Smhd;
import com.ttProject.container.mp4.type.Stbl;
import com.ttProject.container.mp4.type.Stco;
import com.ttProject.container.mp4.type.Stsc;
import com.ttProject.container.mp4.type.Stsd;
import com.ttProject.container.mp4.type.Stss;
import com.ttProject.container.mp4.type.Stsz;
import com.ttProject.container.mp4.type.Stts;
import com.ttProject.container.mp4.type.Tfhd;
import com.ttProject.container.mp4.type.Tfra;
import com.ttProject.container.mp4.type.Tkhd;
import com.ttProject.container.mp4.type.Traf;
import com.ttProject.container.mp4.type.Trak;
import com.ttProject.container.mp4.type.Trun;
import com.ttProject.container.mp4.type.Udta;
import com.ttProject.container.mp4.type.Vmhd;
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
		switch(Type.getType(name.get())) {
		case Ftyp:
			atom = new Ftyp(size, name);
			break;
		case Moov:
			atom = new Moov(size, name);
			break;
		case Mvhd:
			atom = new Mvhd(size, name);
			break;
		case Iods:
			atom = new Iods(size, name);
			break;
		case Trak:
			atom = new Trak(size, name);
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
		case Vmhd:
			atom = new Vmhd(size, name);
			break;
		case Smhd:
			atom = new Smhd(size, name);
			break;
		case Hmhd:
			atom = new Hmhd(size, name);
			break;
		case Nmhd:
			atom = new Nmhd(size, name);
			break;
		case Dinf:
			atom = new Dinf(size, name);
			break;
		case Dref:
			atom = new Dref(size, name);
			break;
		case Stbl:
			atom = new Stbl(size, name);
			break;
		case Stsd:
			atom = new Stsd(size, name);
			break;
		case Stts:
			atom = new Stts(size, name);
			break;
		case Ctts:
			atom = new Ctts(size, name);
			break;
		case Stsc:
			atom = new Stsc(size, name);
			break;
		case Stsz:
			atom = new Stsz(size, name);
			break;
		case Stco:
			atom = new Stco(size, name);
			break;
		case Co64:
			atom = new Co64(size, name);
			break;
		case Stss:
			atom = new Stss(size, name);
			break;
		case Udta:
			atom = new Udta(size, name);
			break;
		case Moof:
			atom = new Moof(size, name);
			break;
		case Mfhd:
			atom = new Mfhd(size, name);
			break;
		case Traf:
			atom = new Traf(size, name);
			break;
		case Tfhd:
			atom = new Tfhd(size, name);
			break;
		case Trun:
			atom = new Trun(size, name);
			break;
		case Mdat:
			atom = new Mdat(size, name);
			break;
		case Free:
			atom = new Free(size, name);
			break;
		case Skip:
			atom = new Skip(size, name);
			break;
		case Mfra:
			atom = new Mfra(size, name);
			break;
		case Tfra:
			atom = new Tfra(size, name);
			break;
		case Mfro:
			atom = new Mfro(size, name);
			break;
		default:
			logger.info("まだ未定義" + Type.getType(name.get()));
			return null;
		}
		atom.minimumLoad(channel);
		return atom;
	}
}
