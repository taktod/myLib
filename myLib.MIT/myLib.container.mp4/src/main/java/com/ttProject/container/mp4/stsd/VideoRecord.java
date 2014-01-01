package com.ttProject.container.mp4.stsd;

import java.util.List;

import com.ttProject.container.mp4.Mp4Atom;
import com.ttProject.unit.extra.bit.Bit16;
import com.ttProject.unit.extra.bit.Bit32;
import com.ttProject.unit.extra.bit.Bit8;

public abstract class VideoRecord extends DescriptionRecord {
	private Bit8[] reserved1 = new Bit8[6];
	private Bit16 dataReferenceIndex;
	private Bit16 predefined1;
	private Bit16 reserved2;
	private Bit32[] predefined2 = new Bit32[3];
	private Bit16 width;
	private Bit16 height;
	private Bit32 horizontalResolution;
	private Bit32 verticalResolution;
	private Bit32 reserved3;
	private Bit16 frameCount;
	private Bit8[] compressorName = new Bit8[32];
	private Bit16 depth;
	private Bit16 predefined3;
	private List<Mp4Atom> boxes;
	public VideoRecord(Bit32 size, Bit32 name) {
		super(size, name);
	}
}
