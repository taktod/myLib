package com.ttProject.media.mp4.atom.stsd;

import com.ttProject.media.mp4.Atom;
import com.ttProject.media.mp4.atom.stsd.record.Aac;
import com.ttProject.media.mp4.atom.stsd.record.H264;
import com.ttProject.media.mp4.atom.stsd.record.Mp3;

public abstract class Record extends Atom {
	public Record(String name, int position, int size) {
		super(name, position, size);
	}
	public static Record getRecord(String name, int position, int size) throws Exception {
		// mp3なのにここがmp4aになっているデータがあった。コーデックを調べるのにはつかえなさそう
		if(".mp3".equals(name)) {
			return new Mp3(name, position, size);
		}
		else if("mp4a".equals(name)) {
			return new Aac(name, position, size);
		}
		else if("h264".equals(name) || "avc1".equals(name)) {
			return new H264(name, position, size);
		}
		throw new Exception("unknown stsdRecord:" + name);
	}
}
