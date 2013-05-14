package com.ttProject.media.mp4.atom;

import java.nio.ByteBuffer;

import com.ttProject.util.BufferUtil;
import com.ttProject.util.IntUtil;
import com.ttProject.media.mp4.Atom;
import com.ttProject.media.mp4.IAtomAnalyzer;
import com.ttProject.nio.channels.IFileReadChannel;

public class Ftyp extends Atom {
	private int majorBrand;
	private int minorVersion;
	private int[] compatibleBrands;
	public Ftyp(int size, int position) {
		super(Ftyp.class.getSimpleName().toLowerCase(), size, position);
		int counter = (size - 16) / 4;
		compatibleBrands = new int[counter];
	}
	@Override
	public void analyze(IFileReadChannel ch, IAtomAnalyzer analyzer) throws Exception {
		int size = getSize() - 8;
		ch.position(getPosition() + 8);
		ByteBuffer data = BufferUtil.safeRead(ch, size);
		analyzed();
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
		if(isAnalyzed()) {
			data.append("[majorBrand:").append(IntUtil.makeHexString(majorBrand)).append("]");
			data.append("[minorVersion:").append(minorVersion).append("]");
			data.append("[compatibleBrand:");
			for(int i = 0;i < compatibleBrands.length; i ++) {
				data.append(IntUtil.makeHexString(compatibleBrands[i]));
				if(i != compatibleBrands.length - 1) {
					data.append(" ");
				}
			}
			data.append("]");
		}
		else {
			data.append("[data is unanalized yet...]");
		}
		return data.toString();
	}
}
