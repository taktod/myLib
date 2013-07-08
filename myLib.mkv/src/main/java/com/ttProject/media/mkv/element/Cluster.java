package com.ttProject.media.mkv.element;

import com.ttProject.media.mkv.Element;
import com.ttProject.media.mkv.MasterElement;
import com.ttProject.media.mkv.Type;
import com.ttProject.nio.channels.IReadChannel;

public class Cluster extends MasterElement {
	public Cluster(long position, long size, long dataPosition) {
		super(Type.Cluster, position, size, dataPosition);
	}
	public Cluster(IReadChannel ch) throws Exception {
		this(ch.position() - Type.Cluster.tagSize(), Element.getSize(ch), ch.position());
	}
	@Override
	public String toString() {
		return super.toString("  ");
	}
}
