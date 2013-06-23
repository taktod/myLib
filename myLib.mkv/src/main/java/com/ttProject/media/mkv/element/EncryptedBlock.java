package com.ttProject.media.mkv.element;

import com.ttProject.media.mkv.Element;
import com.ttProject.media.mkv.IElementAnalyzer;
import com.ttProject.media.mkv.Type;
import com.ttProject.nio.channels.IFileReadChannel;

public class EncryptedBlock extends Element {
	public EncryptedBlock(long position, long size, long dataPosition) {
		super(Type.EncryptedBlock, position, size, dataPosition);
	}
	public EncryptedBlock(IFileReadChannel ch) throws Exception {
		this(ch.position() - Type.EncryptedBlock.tagSize(), Element.getSize(ch), ch.position());
	}
	@Override
	public void analyze(IFileReadChannel ch, IElementAnalyzer analyzer)
			throws Exception {
	}
	@Override
	public String toString() {
		return super.toString("    ");
	}
}
