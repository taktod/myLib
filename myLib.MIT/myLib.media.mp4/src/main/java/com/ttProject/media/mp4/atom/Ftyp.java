package com.ttProject.media.mp4.atom;

import java.nio.ByteBuffer;

import com.ttProject.util.BufferUtil;
import com.ttProject.util.IntUtil;
import com.ttProject.media.mp4.Atom;
import com.ttProject.media.mp4.IAtomAnalyzer;
import com.ttProject.nio.channels.IReadChannel;

public class Ftyp extends Atom {
	private int majorBrand = -1;
	private int minorVersion = -1;
	private int[] compatibleBrands;
	public Ftyp(int position, int size) {
		super(Ftyp.class.getSimpleName().toLowerCase(), position, size);
		int counter = (size - 16) / 4;
		compatibleBrands = new int[counter];
	}
	@Override
	public void analyze(IReadChannel ch, IAtomAnalyzer analyzer) throws Exception {
		int size = getSize() - 8;
		ch.position(getPosition() + 8);
		ByteBuffer data = BufferUtil.safeRead(ch, size);
		majorBrand = data.getInt();
		minorVersion = data.getInt();
		for(int i = 0;i < compatibleBrands.length;i ++) {
			compatibleBrands[i] = data.getInt();
		}
	}
	@Override
	public String toString() {
		StringBuilder data = new StringBuilder();
		data.append("ftyp");
		if(majorBrand != -1) {
			data.append("[majorBrand:").append(IntUtil.makeHexString(majorBrand)).append("]");
			data.append("[minorVersion:").append(minorVersion).append("]");
			data.append("[compatibleBrand:");
			for(int i = 0;i < compatibleBrands.length; i ++) {
				data.append(IntUtil.makeHexString(compatibleBrands[i]));
				if(i != compatibleBrands.length - 1) {
					data.append(" ");
				}
			}
		}
		data.append("]");
		return data.toString();
	}
}
