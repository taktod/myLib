package com.ttProject.container.mp4.stsd;

import java.util.List;

import com.ttProject.container.mp4.Mp4Atom;
import com.ttProject.unit.extra.bit.Bit16;
import com.ttProject.unit.extra.bit.Bit32;
import com.ttProject.unit.extra.bit.Bit8;

public abstract class AudioRecord extends DescriptionRecord {
	private Bit8[] reserved1 = new Bit8[6];
	private Bit16 dataReferenceIndex;
	private Bit32[] reserved2 = new Bit32[2];
	private Bit16 channelCount;
	private Bit16 sampleSize;
	private Bit16 predefined;
	private Bit16 reserved3;
	private Bit32 sampleRate;
	// この下にBoxが入るみたい。
	private List<Mp4Atom> boxes;
	// boxes
	public AudioRecord(Bit32 size, Bit32 name) {
		super(size, name);
	}
}
