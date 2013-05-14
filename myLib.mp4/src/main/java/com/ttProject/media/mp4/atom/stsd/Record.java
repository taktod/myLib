package com.ttProject.media.mp4.atom.stsd;

import com.ttProject.media.mp4.Atom;
import com.ttProject.media.mp4.atom.stsd.record.Aac;
import com.ttProject.media.mp4.atom.stsd.record.H264;
import com.ttProject.media.mp4.atom.stsd.record.Mp3;

public abstract class Record extends Atom {
	public Record(String name, int size, int position) {
		super(name, size, position);
	}
	public static Record getRecord(String name, int size, int position) throws Exception {
		if(".mp3".equals(name)) {
			return new Mp3(name, size, position);
		}
		else if("mp4a".equals(name)) {
			return new Aac(name, size, position);
		}
		else if("h264".equals(name) || "avc1".equals(name)) {
			return new H264(name, size, position);
		}
		throw new Exception("unknown stsdRecord:" + name);
	}
}
