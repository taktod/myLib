package com.ttProject.container.mp4.stsd;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ttProject.container.IContainer;
import com.ttProject.container.mp4.Mp4Atom;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit16;
import com.ttProject.unit.extra.bit.Bit32;
import com.ttProject.unit.extra.bit.Bit8;

public abstract class VideoRecord extends DescriptionRecord {
	/** ロガー */
	private Logger logger = Logger.getLogger(VideoRecord.class);
	private Bit8[] reserved1 = new Bit8[6];
	private Bit16 dataReferenceIndex = new Bit16();
	private Bit16 predefined1 = new Bit16();
	private Bit16 reserved2 = new Bit16();
	private Bit32[] predefined2 = new Bit32[3];
	private Bit16 width = new Bit16();
	private Bit16 height = new Bit16();
	private Bit32 horizontalResolution = new Bit32();
	private Bit32 verticalResolution = new Bit32();
	private Bit32 reserved3 = new Bit32();
	private Bit16 frameCount = new Bit16();
	private Bit8[] compressorName = new Bit8[32];
	private Bit16 depth = new Bit16();
	private Bit16 predefined3 = new Bit16();
	private List<Mp4Atom> boxes = new ArrayList<Mp4Atom>();
	{
		for(int i = 0;i < 6;i ++) {
			reserved1[i] = new Bit8();
		}
		for(int i = 0;i < 3;i ++) {
			predefined2[i] = new Bit32();
		}
		for(int i = 0;i < 32;i ++) {
			compressorName[i] = new Bit8();
		}
	}
	public VideoRecord(Bit32 size, Bit32 name) {
		super(size, name);
	}
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		super.minimumLoad(channel);
		BitLoader loader = new BitLoader(channel);
		loader.load(reserved1);
		loader.load(dataReferenceIndex, predefined1,
				reserved2);
		loader.load(predefined2);
		loader.load(width, height, horizontalResolution,
				verticalResolution, reserved3, frameCount);
		loader.load(compressorName);
		loader.load(depth, predefined3);
		int targetSize = getSize() - 0x56;
		IContainer container = null;
		StsdAtomReader reader = new StsdAtomReader();
		while(targetSize > 0 && (container = reader.read(channel)) != null) {
			logger.info("みつけたコンテナ:" + container);
			boxes.add((Mp4Atom)container);
			targetSize -= container.getSize();
		}
	}
	@Override
	protected void requestUpdate() throws Exception {
		
	}
}
