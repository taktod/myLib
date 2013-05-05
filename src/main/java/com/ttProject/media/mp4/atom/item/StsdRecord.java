package com.ttProject.media.mp4.atom.item;

import com.ttProject.media.mp4.Atom;

public abstract class StsdRecord extends Atom {
	public StsdRecord(String name, int size, int position) {
		super(name, size, position);
	}
	public static StsdRecord getRecord(String name, int size, int position) throws Exception {
		if(".mp3".equals(name)) {
			return new Mp3Record(name, size, position);
		}
		else if("mp4a".equals(name)) {
			return new AacRecord(name, size, position);
		}
		else if("h264".equals(name) || "avc1".equals(name)) {
			return new H264Record(name, size, position);
		}
		throw new Exception("unknown stsdRecord");
	}
}
