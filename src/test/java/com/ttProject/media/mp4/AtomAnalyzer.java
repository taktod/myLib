package com.ttProject.media.mp4;

import java.lang.reflect.Constructor;
import java.nio.ByteBuffer;

import com.ttProject.nio.channels.IFileReadChannel;

public class AtomAnalyzer implements IAtomAnalyzer {

	public Atom analize(IFileReadChannel ch) throws Exception {
		if(ch.size() == ch.position()) {
			// もうデータがない
			return null;
		}
		int position = ch.position();
		ByteBuffer buffer = ByteBuffer.allocate(8);
		ch.read(buffer);
		buffer.flip();
		int size = buffer.getInt();
		byte[] name = new byte[4];
		buffer.get(name);
		String tag = (new String(name)).intern().toLowerCase();
		try {
			Class<?> cls = Thread.currentThread().getContextClassLoader().loadClass(getClassName(tag));
			Atom atom = null;
			if(cls != null) {
				Constructor<?> construct = cls.getConstructor(new Class<?>[]{int.class, int.class});
				atom = (Atom)construct.newInstance(new Object[]{size, position});
				atom.analyze(ch, this);
			}
			return atom;
		}
		catch(Exception e) {
			System.out.println(tag);
			return null;
		}
		finally{
			ch.position(position + size);
		}
	}
	public String getClassName(String tag) {
		return "com.ttProject.media.mp4." + tag.substring(0, 1).toUpperCase() + tag.substring(1).toLowerCase();
	}
}
