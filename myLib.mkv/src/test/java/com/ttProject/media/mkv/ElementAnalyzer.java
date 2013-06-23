package com.ttProject.media.mkv;

import java.lang.reflect.Constructor;

import com.ttProject.media.mkv.element.Cluster;
import com.ttProject.nio.channels.IFileReadChannel;

public class ElementAnalyzer implements IElementAnalyzer {
	@Override
	public Element analyze(IFileReadChannel ch) throws Exception {
		if(ch.size() == ch.position()) {
			return null;
		}
		Type tag = Element.getTag(ch);
		System.out.println(tag);
		try {
			Class<?> cls = Thread.currentThread().getContextClassLoader().loadClass(getClassName(tag));
			Element element = null;
			if(cls != null) {
				Constructor<?> construct = cls.getConstructor(new Class<?>[]{IFileReadChannel.class});
				element = (Element)construct.newInstance(new Object[]{ch});
				element.analyze(ch, this);
				ch.position((int)(element.getDataPosition() + element.getSize()));
			}
			return element;
		}
		catch (Exception e) {
			System.out.println(tag);
//			System.exit(0);
			return null;
		}
	}
	private String getClassName(Type type) {
		return "com.ttProject.media.mkv.element." + type.toString();
	}
}
