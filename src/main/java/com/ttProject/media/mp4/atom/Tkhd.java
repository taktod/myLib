package com.ttProject.media.mp4.atom;

import com.ttProject.media.mp4.Atom;
import com.ttProject.media.mp4.IAtomAnalyzer;
import com.ttProject.nio.channels.IFileReadChannel;

public class Tkhd extends Atom {
	private byte version;
	private int flags;
	private long creationTime;
	private long modificationTime;
	private int trackId;
	private int reserved1;
	private long duration;
	private int reserved2;
	private short layer;
	private short alternateGroup;
	private short volume;
	private short reserved3;
	private int[] transformMatrix = new int[9];
	private int width;
	private int height;
	public Tkhd(int size, int position) {
		super(Tkhd.class.getSimpleName().toLowerCase(), size, position);
	}
	@Override
	public void analyze(IFileReadChannel ch, IAtomAnalyzer analyzer) throws Exception {
		// tkhdの中身を解析していく。
		System.out.println("tkhdの中身なんとかしとく。");
	}
	@Override
	public String toString() {
		return super.toString("    ");
	}
}
